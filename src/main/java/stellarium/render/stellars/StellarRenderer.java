package stellarium.render.stellars;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import net.minecraft.client.renderer.GLAllocation;
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
import stellarium.render.stellars.layer.LayerRHelper;
import stellarium.render.stellars.phased.StellarPhasedRenderer;
import stellarium.render.util.FramebufferCustom;
import stellarium.util.MCUtil;
import stellarium.util.OpenGlUtil;
import stellarium.view.ViewerInfo;

/**
 * Renderer for various visual effects
 * */
public enum StellarRenderer {
	INSTANCE;

	private FramebufferCustom sky = null, scopeTemp = null, brQuery = null, eyed = null;
	private int prevWidth = 0, prevHeight = 0;
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

	private UtilShaders shaders = new UtilShaders();

	public void initialize(ClientSettings settings) {
		AtmosphereSettings atmSettings = (AtmosphereSettings) settings.getSubConfig(AtmosphereSettings.KEY);
		AtmosphereRenderer.INSTANCE.initialize(atmSettings);

		this.setupShader();
		this.setupPixelBuffer();
	}

	public void setupPixelBuffer() {
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
		if(this.scopeTemp != null)
			scopeTemp.deleteFramebuffer();
		if(this.brQuery != null)
			brQuery.deleteFramebuffer();
		if(this.eyed != null)
			eyed.deleteFramebuffer();

		this.sky = FramebufferCustom.builder()
				.texFormat(OpenGlUtil.RGB16F, GL11.GL_RGB, OpenGlUtil.TEXTURE_FLOAT)
				.depthStencil(true, false)
				.build(width, height);

		this.scopeTemp = FramebufferCustom.builder()
				.texFormat(OpenGlUtil.RGB16F, GL11.GL_RGB, OpenGlUtil.TEXTURE_FLOAT)
				.depthStencil(false, false)
				.build(width, height);

		this.brQuery = FramebufferCustom.builder()
				.texFormat(OpenGlUtil.RGB16F, GL11.GL_RGB, OpenGlUtil.TEXTURE_FLOAT)
				.renderRegion(0, 0, width, height)
				.texMinMagFilter(GL11.GL_NEAREST_MIPMAP_NEAREST, GL11.GL_NEAREST)
				.depthStencil(false, false)
				.build(texSize, texSize);

		this.eyed = FramebufferCustom.builder()
				.texFormat(OpenGlUtil.RGB16F, GL11.GL_RGB, OpenGlUtil.TEXTURE_FLOAT)
				.depthStencil(false, false)
				.build(width, height);
	}

	public void setupShader() {
		this.scope = ShaderHelper.getInstance().buildShader("Scope",
				StellarSkyResources.vertexScope, StellarSkyResources.fragmentScope);
		scope.getField("texture").setInteger(0);
		this.fieldBrMult = scope.getField("brightnessMult");
		this.fieldResDir = scope.getField("resDirection");

		this.skyToQueried = ShaderHelper.getInstance().buildShader("SkyToQueried",
				StellarSkyResources.vertexSkyToQueried, StellarSkyResources.fragmentSkyToQueried);
		skyToQueried.getField("texture").setInteger(0);
		this.fieldRelative = skyToQueried.getField("relative");

		this.hdrToldr = ShaderHelper.getInstance().buildShader("HDRtoLDR",
				StellarSkyResources.vertexHDRtoLDR, StellarSkyResources.fragmentHDRtoLDR);
		hdrToldr.getField("texture").setInteger(0);
		this.fieldBrScale = hdrToldr.getField("brScale");

		this.linearToSRGB = ShaderHelper.getInstance().buildShader("linearToSRGB",
				StellarSkyResources.vertexLinearToSRGB, StellarSkyResources.fragmentLinearToSRGB);
		linearToSRGB.getField("texture").setInteger(0);

		shaders.reloadShaders();
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

		// Scope effects
		// TODO Every scope effects should come here
		// MAYBE Separate resolution for each of R, G, B component when wavelengths are far apart
		// MAYBE Apply star-shaped blur for eye
		ViewerInfo viewer = info.info;
		double multRed = viewer.colorMultiplier.getX() / Spmath.sqr(viewer.multiplyingPower);
		double multGreen = viewer.colorMultiplier.getY() / Spmath.sqr(viewer.multiplyingPower);
		double multBlue = viewer.colorMultiplier.getZ() / Spmath.sqr(viewer.multiplyingPower);

		// MAYBE Calculate blur for each pixel
		// Blur on X-axis
		scopeTemp.bindFramebuffer(false);
		scopeTemp.framebufferClear();

		scope.bindShader();
		fieldBrMult.setDouble4(1.0, 1.0, 1.0, 1.0);
		fieldResDir.setDouble2(viewer.resolutionGeneral / info.relativeWidth, 0.0);
		sky.bindFramebufferTexture();
		sky.renderFullQuad();
		scope.releaseShader();

		// Blur on Y-axis and Light Power
		sky.bindFramebuffer(false);
		sky.framebufferClear();

		scope.bindShader();
		fieldBrMult.setDouble4(multRed, multGreen, multBlue, 1.0);
		fieldResDir.setDouble2(0.0, viewer.resolutionGeneral / info.relativeHeight);
		scopeTemp.bindFramebufferTexture();
		scopeTemp.renderFullQuad();
		scope.releaseShader();

		// Visual effects
		// Brightness Query
		if(currentTime < this.prevTime || currentTime >= this.prevTime + 100) {
			// Render to Brightness Query
			brQuery.bindFramebuffer(true);
			brQuery.framebufferClear();

			skyToQueried.bindShader();
			fieldRelative.setDouble3(info.relativeWidth, info.relativeHeight, 1.0f);
			sky.bindFramebufferTexture();
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
				this.brightness += (currentBrightness - this.brightness) * 0.1f;
			}

			GL15.glUnmapBuffer(OpenGlUtil.PIXEL_PACK_BUFFER);
			GL15.glBindBuffer(OpenGlUtil.PIXEL_PACK_BUFFER, 0);

			this.prevTime = currentTime;
		}

		// HDR to LDR
		eyed.bindFramebuffer(true);
		eyed.framebufferClear();


		hdrToldr.bindShader();
		fieldBrScale.setDouble(Math.max(Math.min(
				Math.pow(4.5 * this.brightness, 0.5) * 10.0, 1000.0), 1.0));
		sky.bindFramebufferTexture();
		sky.renderFullQuad();
		hdrToldr.releaseShader();

		// Linear RGB to sRGB
		OpenGlUtil.bindFramebuffer(OpenGlUtil.FRAMEBUFFER_GL, this.prevFramebufferBound);

		linearToSRGB.bindShader();
		eyed.bindFramebufferTexture();
		eyed.renderFullQuad();
		linearToSRGB.releaseShader();
	}

	public void render(StellarModel model, StellarRI info) {
		LayerRHelper layerInfo = new LayerRHelper(info, this.shaders);

		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE);

		// Pre-process
		this.preProcess();

		// TODO AA Use better value for positions

		// Prepare
		AtmosphereRenderer.INSTANCE.render(model.atmModel, EnumAtmospherePass.Prepare, info);

		// Render surface
		StellarPhasedRenderer.INSTANCE.render(model.layersModel, EnumStellarPass.Source, layerInfo);

		// Setup opaque
		GlStateManager.enableDepth();
		GlStateManager.depthMask(true);
		GlStateManager.disableBlend();
		// Render opaque
		StellarPhasedRenderer.INSTANCE.render(model.layersModel, EnumStellarPass.Opaque, layerInfo);

		GlStateManager.disableDepth();
		GlStateManager.depthMask(false);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE);

		GlStateManager.shadeModel(GL11.GL_FLAT);

		// Prepare dominate scatter
		AtmosphereRenderer.INSTANCE.render(model.atmModel, EnumAtmospherePass.SetupDominateScatter, info);
		layerInfo.apply(info);
		// Render dominate scatter
		StellarPhasedRenderer.INSTANCE.render(model.layersModel, EnumStellarPass.DominateScatter, layerInfo);

		// Finalize
		AtmosphereRenderer.INSTANCE.render(model.atmModel, EnumAtmospherePass.Finalize, info);

		// Post-process
		this.postProcess(info);

		// State setup
		GlStateManager.shadeModel(GL11.GL_FLAT);
		GlStateManager.depthMask(true);
		GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);
		GlStateManager.enableDepth();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}
}
