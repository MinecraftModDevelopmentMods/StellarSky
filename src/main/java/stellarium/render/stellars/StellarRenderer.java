package stellarium.render.stellars;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
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

	private FramebufferCustom sky, temporary;
	private int prevWidth = 0, prevHeight = 0;
	private int prevFramebufferBound;

	private IShaderObject hdrToldr, linearToSRGB;
	private IUniformField fieldBr;

	private int maxLevel;
	private float scrMult;
	private FloatBuffer brightness;

	public void initialize(ClientSettings settings) {
		AtmosphereSettings atmSettings = (AtmosphereSettings) settings.getSubConfig(AtmosphereSettings.KEY);
		AtmosphereRenderer.INSTANCE.initialize(atmSettings);

		this.brightness = ByteBuffer.allocateDirect(3 << 2).order(ByteOrder.nativeOrder()).asFloatBuffer();
		this.setupShader();
	}

	private static int log2(int n) {
		return 31 - Integer.numberOfLeadingZeros(n);
	}

	public void setupFramebuffer(int width, int height) {
		this.maxLevel = log2(Math.max(width, height) - 1) + 1;
		int texSize = 1 << this.maxLevel;
		this.scrMult = (float)(width * height) / (texSize * texSize);

		this.sky = FramebufferCustom.builder()
				.texFormat(OpenGlUtil.RGB16F, GL11.GL_RGB, OpenGlUtil.TEXTURE_FLOAT)
				.renderRegion(0, 0, width, height)
				.texMinMagFilter(GL11.GL_NEAREST_MIPMAP_NEAREST, GL11.GL_NEAREST)
				.depthStencil(true, false)
				.build(texSize, texSize);

		this.temporary = FramebufferCustom.builder()
				.texFormat(OpenGlUtil.RGB16F, GL11.GL_RGB, OpenGlUtil.TEXTURE_FLOAT)
				.depthStencil(false, false)
				.build(width, height);
	}

	public void setupShader() {
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

	private static final StellarTessellator tessellator = new StellarTessellator();

	public void preProcess() {
		// TODO AA change to manual rendering
		// TODO AA Try to lessen the Brightness Gap
		// TODO AA Just use vector to specify position

		this.prevFramebufferBound = GlStateManager.glGetInteger(OpenGlUtil.FRAMEBUFFER_BINDING);

		sky.bindFramebuffer(false);
		GlStateManager.depthMask(true);
		sky.framebufferClear();
		GlStateManager.depthMask(false);
	}

	public void postProcess(StellarRI info) {
		// HDR to LDR
		temporary.bindFramebuffer(true);
		temporary.framebufferClear();

		sky.bindFramebufferTexture();

		brightness.clear();
		OpenGlUtil.generateMipmap(GL11.GL_TEXTURE_2D);
		GL11.glGetTexImage(GL11.GL_TEXTURE_2D, this.maxLevel, GL11.GL_RGB, GL11.GL_FLOAT, this.brightness);
		// TODO More accuracy needed, currently too dependent on the Sun and the perspective.
		//int i = GlStateManager.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, this.maxLevel, GL11.GL_TEXTURE_WIDTH);
		//int j = GlStateManager.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, this.maxLevel, GL11.GL_TEXTURE_HEIGHT);
		float br = (brightness.get(0) * 0.2126f
				+ brightness.get(1) * 0.7152f
				+ brightness.get(2) * 0.0722f) * this.scrMult;

		hdrToldr.bindShader();
		fieldBr.setDouble(br);
		sky.renderFullQuad();
		//RenderUtil.renderFullQuad();
		hdrToldr.releaseShader();


		// Linear RGB to sRGB
		OpenGlUtil.bindFramebuffer(OpenGlUtil.FRAMEBUFFER_GL, this.prevFramebufferBound);

		linearToSRGB.bindShader();
		temporary.bindFramebufferTexture();
		RenderUtil.renderFullQuad();
		linearToSRGB.releaseShader();
	}

	public void render(StellarModel model, StellarRI info) {
		LayerRI layerInfo = new LayerRI(info, tessellator);

		// Pre-process
		this.preProcess();

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
