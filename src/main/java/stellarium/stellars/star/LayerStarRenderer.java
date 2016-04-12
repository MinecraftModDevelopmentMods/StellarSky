package stellarium.stellars.star;

import org.lwjgl.opengl.GL11;

import net.minecraft.util.ResourceLocation;
import stellarium.StellarSkyResources;
import stellarium.api.PerDimensionResource;
import stellarium.render.ICelestialLayerRenderer;
import stellarium.render.StellarRenderInfo;

public class LayerStarRenderer implements ICelestialLayerRenderer {

	@Override
	public void preRender(StellarRenderInfo info) {
		GL11.glColor4f(1.0f, 1.0f, 1.0f, info.weathereff);

		info.mc.renderEngine.bindTexture(StellarSkyResources.resourceStar.getLocationFor(info.mc.theWorld));
		info.tessellator.startDrawingQuads();
	}

	@Override
	public void postRender(StellarRenderInfo info) {
		info.tessellator.draw();
	}

}
