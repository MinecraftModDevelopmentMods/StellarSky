package stellarium.render.stellars;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL30;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import stellarium.client.ClientSettings;
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

	private FramebufferCustom sky = null;
	private FramebufferCustom srgbFBO = null;
	private int prevWidth = 0, prevHeight = 0;
	private int prevFramebufferBound;

	public void initialize(ClientSettings settings) {
		AtmosphereSettings atmSettings = (AtmosphereSettings) settings.getSubConfig(AtmosphereSettings.KEY);
		AtmosphereRenderer.INSTANCE.initialize(atmSettings);
	}

	public void preRender(ClientSettings settings, StellarRI info) {
		AtmosphereSettings atmSettings = (AtmosphereSettings) settings.getSubConfig(AtmosphereSettings.KEY);
		AtmosphereRenderer.INSTANCE.preRender(atmSettings, info);

		Framebuffer mcBuffer = info.minecraft.getFramebuffer();
		if(mcBuffer.framebufferWidth != this.prevWidth || mcBuffer.framebufferHeight != this.prevHeight) {
			this.setupConverter(mcBuffer.framebufferWidth, mcBuffer.framebufferHeight);
			this.prevWidth = mcBuffer.framebufferWidth;
			this.prevHeight = mcBuffer.framebufferHeight;
		}
	}

	public void setupConverter(int width, int height) {
		this.sky = FramebufferCustom.builder()
				.setupTexFormat(OpenGlUtil.RGB16F, GL11.GL_RGB, OpenGlUtil.TEXTURE_FLOAT)
				.setupDepthStencil(true, false)
				.build(width, height);

		this.srgbFBO = FramebufferCustom.builder()
				.setupTexFormat(GL21.GL_SRGB8, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE)
				.setupDepthStencil(false, false)
				.build(width, height);
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
		GL11.glEnable(GL30.GL_FRAMEBUFFER_SRGB);
		srgbFBO.bindFramebuffer(false);
		srgbFBO.framebufferClear();

		sky.bindFramebufferTexture();
		RenderUtil.renderFullQuad();
		GL11.glDisable(GL30.GL_FRAMEBUFFER_SRGB);

		srgbFBO.bindFramebuffer(GL30.GL_READ_FRAMEBUFFER);
		OpenGlUtil.bindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, this.prevFramebufferBound);

		Framebuffer mcBuffer = info.minecraft.getFramebuffer();
		int width = mcBuffer.framebufferWidth;
		int height = mcBuffer.framebufferHeight;
		GL30.glBlitFramebuffer(0, 0, width, height, 0, 0, width, height,
				GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST);

		OpenGlUtil.bindFramebuffer(OpenGlUtil.FRAMEBUFFER_GL, this.prevFramebufferBound);
	}

	public void render(StellarModel model, StellarRI info) {
		LayerRI layerInfo = new LayerRI(info, tessellator);

		// Pre-process
		this.preProcess();

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
		AtmosphereRenderer.INSTANCE.render(model.atmModel, EnumAtmospherePass.PrepareDominateScatter, info);
		// Render dominate scatter
		StellarPhasedRenderer.INSTANCE.render(model.layersModel, EnumStellarPass.DominateScatter, layerInfo);
		// Finalize dominate scatter
		AtmosphereRenderer.INSTANCE.render(model.atmModel, EnumAtmospherePass.FinalizeDominateScatter, info);

		// Post-process
		this.postProcess(info);

		// State setup
		ShaderHelper.getInstance().releaseCurrentShader();
		GlStateManager.depthMask(true);
		GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);
		GlStateManager.enableDepth();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}
}
