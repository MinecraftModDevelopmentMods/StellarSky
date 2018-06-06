package stellarium.render.stellars;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL21;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import stellarapi.api.lib.math.Spmath;
import stellarium.StellarSky;
import stellarium.StellarSkyResources;
import stellarium.client.ClientSettings;
import stellarium.render.shader.IShaderObject;
import stellarium.render.shader.IUniformField;
import stellarium.render.shader.ShaderHelper;
import stellarium.render.stellars.access.EnumStellarPass;
import stellarium.render.stellars.atmosphere.AtmosphereRenderer;
import stellarium.render.stellars.atmosphere.AtmosphereSettings;
import stellarium.render.stellars.atmosphere.EnumAtmospherePass;
import stellarium.render.stellars.atmosphere.FramebufferCustom;
import stellarium.render.stellars.layer.LayerRI;
import stellarium.render.stellars.phased.StellarPhasedRenderer;
import stellarium.util.OpenGlUtil;
import stellarium.util.RenderUtil;

public enum StellarRenderer {
	INSTANCE;

	private FramebufferCustom sky = null, brQuery = null, ldrSky = null;
	private int prevWidth = 0, prevHeight = 0;
	private int prevFramebufferBound;

	private IShaderObject skyToQueried, hdrToldr, linearToSRGB;
	private IUniformField fieldBr, fieldRelative;

	private int maxLevel;
	private float screenRatio;
	private int[] pBuffer;
	private ByteBuffer brBuffer = null;

	private long prevTime = -1;
	private int index = 0;
	private float brightness = 0.0f;

	public void initialize(ClientSettings settings) {
		AtmosphereSettings atmSettings = (AtmosphereSettings) settings.getSubConfig(AtmosphereSettings.KEY);
		AtmosphereRenderer.INSTANCE.initialize(atmSettings);

		this.setupShader();

		this.pBuffer = new int[] {GL15.glGenBuffers(), GL15.glGenBuffers()};
		GL15.glBindBuffer(OpenGlUtil.PIXEL_PACK_BUFFER, this.pBuffer[0]);
		GL15.glBufferData(OpenGlUtil.PIXEL_PACK_BUFFER, 3 << 2, GL15.GL_STREAM_READ);
		GL15.glBindBuffer(OpenGlUtil.PIXEL_PACK_BUFFER, this.pBuffer[1]);
		GL15.glBufferData(OpenGlUtil.PIXEL_PACK_BUFFER, 3 << 2, GL15.GL_STREAM_READ);
		GL15.glBindBuffer(OpenGlUtil.PIXEL_PACK_BUFFER, 0);
	}

	private static int log2(int n) {
		return 31 - Integer.numberOfLeadingZeros(n);
	}

	public void setupFramebuffer(int width, int height) {
		this.maxLevel = log2(Math.max(width, height) - 1) + 1;
		int texSize = 1 << this.maxLevel;
		this.screenRatio = (float)(width * height) / (texSize * texSize);

		if(this.sky != null)
			sky.deleteFramebuffer();
		if(this.brQuery != null)
			brQuery.deleteFramebuffer();
		if(this.ldrSky != null)
			ldrSky.deleteFramebuffer();

		this.sky = FramebufferCustom.builder()
				.texFormat(OpenGlUtil.RGB16F, GL11.GL_RGB, OpenGlUtil.TEXTURE_FLOAT)
				.depthStencil(true, false)
				.build(width, height);

		this.brQuery = FramebufferCustom.builder()
				.texFormat(OpenGlUtil.RGB16F, GL11.GL_RGB, OpenGlUtil.TEXTURE_FLOAT)
				.renderRegion(0, 0, width, height)
				.texMinMagFilter(GL11.GL_NEAREST_MIPMAP_NEAREST, GL11.GL_NEAREST)
				.depthStencil(false, false)
				.build(texSize, texSize);

		this.ldrSky = FramebufferCustom.builder()
				.texFormat(OpenGlUtil.RGB16F, GL11.GL_RGB, OpenGlUtil.TEXTURE_FLOAT)
				.depthStencil(false, false)
				.build(width, height);
	}

	public void setupShader() {
		this.skyToQueried = ShaderHelper.getInstance().buildShader("SkyToQueried",
				StellarSkyResources.vertexSkyToQueried, StellarSkyResources.fragmentSkyToQueried);
		skyToQueried.getField("texture").setInteger(0);
		this.fieldRelative = skyToQueried.getField("relative");

		this.hdrToldr = ShaderHelper.getInstance().buildShader("HDRtoLDR",
				StellarSkyResources.vertexHDRtoLDR, StellarSkyResources.fragmentHDRtoLDR);
		hdrToldr.getField("texture").setInteger(0);
		this.fieldBr = hdrToldr.getField("brightness");

		this.linearToSRGB = ShaderHelper.getInstance().buildShader("linearToSRGB",
				StellarSkyResources.vertexLinearToSRGB, StellarSkyResources.fragmentLinearToSRGB);
		linearToSRGB.getField("texture").setInteger(0);
	}

	public void preRender(ClientSettings settings, StellarRI info) {
		AtmosphereSettings atmSettings = (AtmosphereSettings) settings.getSubConfig(AtmosphereSettings.KEY);
		AtmosphereRenderer.INSTANCE.preRender(atmSettings, info);

		Framebuffer mcBuffer = info.minecraft.getFramebuffer();
		if(mcBuffer.framebufferWidth != this.prevWidth || mcBuffer.framebufferHeight != this.prevHeight) {
			this.setupFramebuffer(mcBuffer.framebufferWidth, mcBuffer.framebufferHeight);
			this.prevWidth = mcBuffer.framebufferWidth;
			this.prevHeight = mcBuffer.framebufferHeight;
		}
	}

	private static final CRenderHelper tessellator = new CRenderHelper();

	public void preProcess() {
		this.prevFramebufferBound = GlStateManager.glGetInteger(OpenGlUtil.FRAMEBUFFER_BINDING);

		sky.bindFramebuffer(false);
		GlStateManager.depthMask(true);
		sky.framebufferClear();
		GlStateManager.depthMask(false);
	}

	public void postProcess(StellarRI info) {
		// TODO Render everything on floating framebuffers

		// Extract things needed
		long currentTime = System.currentTimeMillis();
		float relHeight = 2 * Spmath.tand(0.5f *
				RenderUtil.getFOVModifier(info.minecraft.entityRenderer, info.partialTicks, true));
		float relWidth = (relHeight * info.minecraft.displayWidth) / info.minecraft.displayHeight;

		// Brightness Query
		if(currentTime < this.prevTime || currentTime >= this.prevTime + 100) {
			// Render to Brightness Query
			brQuery.bindFramebuffer(true);
			brQuery.framebufferClear();

			sky.bindFramebufferTexture();

			skyToQueried.bindShader();
			fieldRelative.setDouble3(relWidth, relHeight, 1.0f);
			sky.renderFullQuad();
			skyToQueried.releaseShader();

			// Actual Calculation
			brQuery.bindFramebufferTexture();
			OpenGlUtil.generateMipmap(GL11.GL_TEXTURE_2D);

			this.index = (this.index + 1) % 2;
			int nextIndex = (this.index + 1) % 2;

			GL15.glBindBuffer(OpenGlUtil.PIXEL_PACK_BUFFER, this.pBuffer[this.index]);
			GL11.glGetTexImage(GL11.GL_TEXTURE_2D, this.maxLevel, GL11.GL_RGB, GL11.GL_FLOAT, 0);

			GL15.glBindBuffer(OpenGlUtil.PIXEL_PACK_BUFFER, this.pBuffer[nextIndex]);
			this.brBuffer = GL15.glMapBuffer(OpenGlUtil.PIXEL_PACK_BUFFER, GL15.GL_READ_ONLY, this.brBuffer);

			if(this.brBuffer != null) {
				FloatBuffer brBufferF = brBuffer.asFloatBuffer();
				float currentBrightness = (brBufferF.get(0) * 0.2126f
						+ brBufferF.get(1) * 0.7152f
						+ brBufferF.get(2) * 0.0722f) / this.screenRatio;
				this.brightness += (currentBrightness - this.brightness) * 0.03f;
			}

			GL15.glUnmapBuffer(OpenGlUtil.PIXEL_PACK_BUFFER);
			GL15.glBindBuffer(OpenGlUtil.PIXEL_PACK_BUFFER, 0);

			this.prevTime = currentTime;
		}

		// HDR to LDR
		ldrSky.bindFramebuffer(true);
		ldrSky.framebufferClear();

		sky.bindFramebufferTexture();

		hdrToldr.bindShader();
		fieldBr.setDouble(this.brightness);
		sky.renderFullQuad();
		hdrToldr.releaseShader();

		// Linear RGB to sRGB
		OpenGlUtil.bindFramebuffer(OpenGlUtil.FRAMEBUFFER_GL, this.prevFramebufferBound);

		linearToSRGB.bindShader();
		ldrSky.bindFramebufferTexture();
		ldrSky.renderFullQuad();
		linearToSRGB.releaseShader();
	}

	public void render(StellarModel model, StellarRI info) {
		LayerRI layerInfo = new LayerRI(info, tessellator);

		// Pre-process
		this.preProcess();

		// TODO AA Just use vector to specify position, and use better value for positions
		// TODO AA Better handling for light gathering power / resolution changes


		AtmosphereRenderer.INSTANCE.render(model.atmModel, EnumAtmospherePass.Prepare, info);

		// Setup surface scatter
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		AtmosphereRenderer.INSTANCE.render(model.atmModel, EnumAtmospherePass.SetupSurfaceScatter, info);
		// Render surface scatter
		StellarPhasedRenderer.INSTANCE.render(model.layersModel, EnumStellarPass.SurfaceScatter, layerInfo);

		// Setup point scatter
		OpenGlUtil.enablePointSprite();
		GlStateManager.shadeModel(GL11.GL_FLAT);
		AtmosphereRenderer.INSTANCE.render(model.atmModel, EnumAtmospherePass.SetupPointScatter, info);
		// Render point scatter
		StellarPhasedRenderer.INSTANCE.render(model.layersModel, EnumStellarPass.PointScatter, layerInfo);

		// Setup opaque
		OpenGlUtil.disablePointSprite();
		GlStateManager.enableDepth();
		GlStateManager.depthMask(true);
		GlStateManager.disableBlend();
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		AtmosphereRenderer.INSTANCE.render(model.atmModel, EnumAtmospherePass.SetupOpaque, info);
		// Render opaque
		StellarPhasedRenderer.INSTANCE.render(model.layersModel, EnumStellarPass.Opaque, layerInfo);

		// Setup opaque scatter
		GlStateManager.depthMask(false);
		GlStateManager.disableDepth();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE);
		GlStateManager.shadeModel(GL11.GL_FLAT);
		AtmosphereRenderer.INSTANCE.render(model.atmModel, EnumAtmospherePass.SetupOpaqueScatter, info);
		// Render opaque scatter
		StellarPhasedRenderer.INSTANCE.render(model.layersModel, EnumStellarPass.OpaqueScatter, layerInfo);

		// Prepare dominate scatter
		AtmosphereRenderer.INSTANCE.render(model.atmModel, EnumAtmospherePass.SetupDominateScatter, info);
		// Render dominate scatter
		StellarPhasedRenderer.INSTANCE.render(model.layersModel, EnumStellarPass.DominateScatter, layerInfo);

		// Finalize dominate scatter
		AtmosphereRenderer.INSTANCE.render(model.atmModel, EnumAtmospherePass.Finalize, info);

		// Post-process
		this.postProcess(info);

		// State setup
		GlStateManager.depthMask(true);
		GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);
		GlStateManager.enableDepth();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}
}
