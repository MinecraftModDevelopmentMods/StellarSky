package stellarium.stellars.star;

import org.lwjgl.opengl.GL11;

import stellarium.StellarSkyResources;
import stellarium.render.stellars.access.IStellarTessellator;
import stellarium.stellars.render.EnumRenderPass;
import stellarium.stellars.render.ICelestialLayerRenderer;
import stellarium.stellars.render.StellarRenderInfo;

public class LayerStarRenderer implements ICelestialLayerRenderer {

	@Override
	public void preRender(IStellarTessellator tessellator, boolean forOpaque, boolean hasTexture) {
		tessellator.bindTexture(StellarSkyResources.resourceStar.getLocation());
		tessellator.begin(false);
	}

	@Override
	public void postRender(IStellarTessellator tessellator, boolean forOpaque, boolean hasTexture) {
		tessellator.end();
	}

	@Override
	public boolean acceptPass(boolean forOpaque, boolean hasTexture) {
		return !forOpaque && !hasTexture;
	}

}
