package stellarium.render.stellars;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

import net.minecraft.client.renderer.GlStateManager;
import stellarapi.api.optics.EyeDetector;
import stellarium.StellarSkyResources;
import stellarium.render.shader.IShaderObject;
import stellarium.render.shader.IUniformField;
import stellarium.render.shader.ShaderHelper;
import stellarium.render.util.FramebufferCustom;
import stellarium.util.OpenGlUtil;
import stellarium.view.ViewerInfo;

public class PostProcess {
	private FramebufferCustom frame1 = null, frame2 = null, brQuery = null;
	private int prevFramebufferBound;

	private IShaderObject scope, skyToQueried, hdrToldr, linearToSRGB;
	private IUniformField fieldBrMult, fieldResDir, fieldBrScale, fieldRelative;

	private int maxLevel;
	private float screenRatio;
	private int[] pBuffer;
	private ByteBuffer brBuffer = null;

	private long prevTime = -1;
	private int index = 0;
	private float brightness = 0.0f;

	public void initialize() {
		this.setupShader();
		this.setupPixelBuffer();
	}

	public void onResize(int width, int height) {
		this.maxLevel = log2(Math.max(width, height) - 1) + 1;
		int texSize = 1 << this.maxLevel;
		this.screenRatio = (float)(width * height) / (texSize * texSize);

		if(this.frame1 != null)
			frame1.deleteFramebuffer();
		if(this.frame2 != null)
			frame2.deleteFramebuffer();
		if(this.brQuery != null)
			brQuery.deleteFramebuffer();

		// RGBE format
		this.frame1 = FramebufferCustom.builder()
				.texFormat(GL11.GL_RGBA8, GL11.GL_RGBA, GL11.GL_BYTE)
				.depthStencil(false, false)
				.build(width, height);

		// RGBE format
		this.frame2 = FramebufferCustom.builder()
				.texFormat(GL11.GL_RGBA8, GL11.GL_RGBA, GL11.GL_BYTE)
				.depthStencil(false, false)
				.build(width, height);

		this.brQuery = FramebufferCustom.builder()
				.texFormat(OpenGlUtil.RGBA16F, GL11.GL_RGBA, OpenGlUtil.TEXTURE_FLOAT)
				.renderRegion(0, 0, width, height)
				.texMinMagFilter(GL11.GL_NEAREST_MIPMAP_NEAREST, GL11.GL_NEAREST)
				.depthStencil(false, false)
				.build(texSize, texSize);
	}

	public void setupPixelBuffer() {
		this.pBuffer = new int[] {GL15.glGenBuffers(), GL15.glGenBuffers()};

		GL15.glBindBuffer(OpenGlUtil.PIXEL_PACK_BUFFER, this.pBuffer[0]);
		GL15.glBufferData(OpenGlUtil.PIXEL_PACK_BUFFER, 4 << 2, GL15.GL_STREAM_READ);
		GL15.glBindBuffer(OpenGlUtil.PIXEL_PACK_BUFFER, this.pBuffer[1]);
		GL15.glBufferData(OpenGlUtil.PIXEL_PACK_BUFFER, 4 << 2, GL15.GL_STREAM_READ);
		GL15.glBindBuffer(OpenGlUtil.PIXEL_PACK_BUFFER, 0);
	}

	private static int log2(int n) {
		return 31 - Integer.numberOfLeadingZeros(n);
	}

	public void setupShader() {
		this.scope = ShaderHelper.getInstance().buildShader("Scope",
				StellarSkyResources.vertexScope,
				StellarSkyResources.fragmentScope);
		scope.getField("texture").setInteger(0);
		this.fieldBrMult = scope.getField("brightnessMult");
		this.fieldResDir = scope.getField("resDirection");

		this.skyToQueried = ShaderHelper.getInstance().buildShader("SkyToQueried",
				StellarSkyResources.vertexSkyToQueried,
				StellarSkyResources.fragmentSkyToQueried);
		skyToQueried.getField("texture").setInteger(0);
		this.fieldRelative = skyToQueried.getField("relative");

		this.hdrToldr = ShaderHelper.getInstance().buildShader("HDRtoLDR",
				StellarSkyResources.vertexHDRtoLDR,
				StellarSkyResources.fragmentHDRtoLDR);
		hdrToldr.getField("texture").setInteger(0);
		this.fieldBrScale = hdrToldr.getField("brScale");

		this.linearToSRGB = ShaderHelper.getInstance().buildShader("linearToSRGB",
				StellarSkyResources.vertexLinearToSRGB,
				StellarSkyResources.fragmentLinearToSRGB);
		linearToSRGB.getField("texture").setInteger(0);
	}

	public static float toFloat(short hbits)
	{
	    int mant = hbits & 0x03ff;            // 10 bits mantissa
	    int exp =  hbits & 0x7c00;            // 5 bits exponent
	    if( exp == 0x7c00 )                   // NaN/Inf
	        exp = 0x3fc00;                    // -> NaN/Inf
	    else if( exp != 0 )                   // normalized value
	    {
	        exp += 0x1c000;                   // exp - 15 + 127
	        if( mant == 0 && exp > 0x1c400 )  // smooth transition
	            return Float.intBitsToFloat( ( hbits & 0x8000 ) << 16
	                                            | exp << 13 | 0x3ff );
	    }
	    else if( mant != 0 )                  // && exp==0 -> subnormal
	    {
	        exp = 0x1c400;                    // make it normal
	        do {
	            mant <<= 1;                   // mantissa * 2
	            exp -= 0x400;                 // decrease exp by 1
	        } while( ( mant & 0x400 ) == 0 ); // while not normal
	        mant &= 0x3ff;                    // discard subnormal bit
	    }                                     // else +/-0 -> +/-0
	    return Float.intBitsToFloat(          // combine all parts
	        ( hbits & 0x8000 ) << 16          // sign  << ( 31 - 15 )
	        | ( exp | mant ) << 13 );         // value << ( 23 - 10 )
	}

	public void preProcess() {
		this.prevFramebufferBound = GlStateManager.glGetInteger(OpenGlUtil.FRAMEBUFFER_BINDING);

		frame1.bindFramebuffer(false);
		frame1.framebufferClear();
	}

	public void postProcess(StellarRI info) {
		// TODO Render everything on floating framebuffers
		// TODO Refactor to make everything clean and sweat

		// Extract things needed
		long currentTime = System.currentTimeMillis();

		// Scope effects
		// TODO Every scope effects should come here
		// MAYBE Separate resolution for each of R, G, B component when wavelengths are far apart
		// MAYBE Apply star-shaped blur for eye
		ViewerInfo viewer = info.info;
		double multRed = viewer.colorMultiplier.getX();
		double multGreen = viewer.colorMultiplier.getY();
		double multBlue = viewer.colorMultiplier.getZ();

		// MAYBE Calculate blur for each pixel
		// Blur on X-axis
		frame2.bindFramebuffer(false);
		frame2.framebufferClear();

		double resolution = Math.toRadians(EyeDetector.DEFAULT_RESOLUTION) / viewer.multiplyingPower;

		scope.bindShader();
		fieldBrMult.setDouble4(1.0, 1.0, 1.0, 1.0);
		fieldResDir.setDouble2(resolution / info.relativeWidth, 0.0);
		frame1.bindFramebufferTexture();
		frame1.renderFullQuad();
		scope.releaseShader();

		// Blur on Y-axis and Light Power
		frame1.bindFramebuffer(false);
		frame1.framebufferClear();

		scope.bindShader();
		fieldBrMult.setDouble4(multRed, multGreen, multBlue, 1.0);
		fieldResDir.setDouble2(0.0, resolution / info.relativeHeight);
		frame2.bindFramebufferTexture();
		frame2.renderFullQuad();
		scope.releaseShader();

		// Visual effects
		// Brightness Query
		if(currentTime < this.prevTime || currentTime >= this.prevTime + 50) {
			// Render to Brightness Query
			brQuery.bindFramebuffer(true);
			brQuery.framebufferClear();

			skyToQueried.bindShader();
			fieldRelative.setDouble3(info.relativeWidth, info.relativeHeight, 1.0f);
			frame1.bindFramebufferTexture();
			frame1.renderFullQuad();
			skyToQueried.releaseShader();

			// Actual Calculation
			brQuery.bindFramebufferTexture();
			OpenGlUtil.generateMipmap(GL11.GL_TEXTURE_2D);

			this.index = (this.index + 1) % 2;
			int nextIndex = (this.index + 1) % 2;

			GL15.glBindBuffer(OpenGlUtil.PIXEL_PACK_BUFFER, this.pBuffer[this.index]);
			GL11.glGetTexImage(GL11.GL_TEXTURE_2D, this.maxLevel, GL12.GL_BGRA, GL30.GL_HALF_FLOAT, 0);

			GL15.glBindBuffer(OpenGlUtil.PIXEL_PACK_BUFFER, this.pBuffer[nextIndex]);
			this.brBuffer = GL15.glMapBuffer(OpenGlUtil.PIXEL_PACK_BUFFER, GL15.GL_READ_ONLY, 4 << 1, this.brBuffer);

			if(this.brBuffer != null) {
				ShortBuffer brBufferS = brBuffer.asShortBuffer();
				short readShBr = brBufferS.get(2);
				float readBr = toFloat(readShBr);
				float currentBrightness = readBr * 1000.0f / this.screenRatio;
				this.brightness += (currentBrightness - this.brightness) * 0.1f;
			}

			GL15.glUnmapBuffer(OpenGlUtil.PIXEL_PACK_BUFFER);
			GL15.glBindBuffer(OpenGlUtil.PIXEL_PACK_BUFFER, 0);

			this.prevTime = currentTime;
		}

		// HDR to LDR
		frame2.bindFramebuffer(true);
		frame2.framebufferClear();

		hdrToldr.bindShader();
		fieldBrScale.setDouble(Math.max(Math.min(
				Math.pow(4.5 * this.brightness, 0.5) * 10.0, 1000.0), 1.0));
		frame1.bindFramebufferTexture();
		frame1.renderFullQuad();
		hdrToldr.releaseShader();

		// Linear RGB to sRGB
		OpenGlUtil.bindFramebuffer(OpenGlUtil.FRAMEBUFFER_GL, this.prevFramebufferBound);

		linearToSRGB.bindShader();
		frame2.bindFramebufferTexture();
		frame2.renderFullQuad();
		linearToSRGB.releaseShader();
	}

}
