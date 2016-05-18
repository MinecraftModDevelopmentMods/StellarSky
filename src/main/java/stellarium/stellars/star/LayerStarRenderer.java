package stellarium.stellars.star;

import org.lwjgl.opengl.GL11;

import stellarium.StellarSkyResources;
import stellarium.render.celesital.EnumRenderPass;
import stellarium.render.celesital.ICelestialLayerRenderer;
import stellarium.render.celesital.StellarRenderInfo;

public class LayerStarRenderer implements ICelestialLayerRenderer {

	@Override
	public void preRender(StellarRenderInfo info) {
		GL11.glColor4f(1.0f, 1.0f, 1.0f, info.weathereff);

		info.mc.renderEngine.bindTexture(StellarSkyResources.resourceStar.getLocation());
		info.tessellator.startDrawingQuads();
	}

	@Override
	public void postRender(StellarRenderInfo info) {
		info.tessellator.draw();
	}

	@Override
	public boolean acceptPass(EnumRenderPass pass) {
		return pass == EnumRenderPass.DeepScattering;
	}

}
