package stellarium.render.stellars;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import stellarium.client.ClientSettings;
import stellarium.render.shader.ShaderHelper;
import stellarium.render.stellars.access.EnumStellarPass;
import stellarium.render.stellars.atmosphere.AtmosphereRenderer;
import stellarium.render.stellars.atmosphere.AtmosphereSettings;
import stellarium.render.stellars.atmosphere.EnumAtmospherePass;
import stellarium.render.stellars.layer.LayerRenderInformation;
import stellarium.render.stellars.phased.StellarPhasedRenderer;
import stellarium.render.stellars.phased.StellarRenderInformation;
import stellarium.util.OpenGlUtil;

public enum StellarRenderer {
	INSTANCE;

	public void initialize(ClientSettings settings) {
		AtmosphereSettings atmSettings = (AtmosphereSettings) settings.getSubConfig(AtmosphereSettings.KEY);
		AtmosphereRenderer.INSTANCE.initialize(atmSettings);
	}

	public void preRender(ClientSettings settings, StellarRenderInformation info) {
		AtmosphereSettings atmSettings = (AtmosphereSettings) settings.getSubConfig(AtmosphereSettings.KEY);
		AtmosphereRenderer.INSTANCE.preRender(atmSettings, info);
	}

	private static final StellarTessellator tessellator = new StellarTessellator();

	public void render(StellarModel model, StellarRenderInformation info) {
		LayerRenderInformation layerInfo = new LayerRenderInformation(info, tessellator);

		// TODO Allow the renderer more performance.
		// Prepare dominate scatter
		AtmosphereRenderer.INSTANCE.render(model.atmModel, EnumAtmospherePass.PrepareDominateScatter, info);
		// Render dominate scatter
		StellarPhasedRenderer.INSTANCE.render(model.layersModel, EnumStellarPass.DominateScatter, layerInfo);
		// Finalize dominate scatter
		AtmosphereRenderer.INSTANCE.render(model.atmModel, EnumAtmospherePass.FinalizeDominateScatter, info);
		// Bind dominate scatter
		AtmosphereRenderer.INSTANCE.render(model.atmModel, EnumAtmospherePass.BindDomination, info);

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

		// Unbind dominate scatter
		AtmosphereRenderer.INSTANCE.render(model.atmModel, EnumAtmospherePass.UnbindDomination, info);
		// Render cached atmosphere
		AtmosphereRenderer.INSTANCE.render(model.atmModel, EnumAtmospherePass.RenderCachedDominate, info);

		ShaderHelper.getInstance().releaseCurrentShader();
		GlStateManager.depthMask(true);
		GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);
		GlStateManager.enableDepth();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}
}
