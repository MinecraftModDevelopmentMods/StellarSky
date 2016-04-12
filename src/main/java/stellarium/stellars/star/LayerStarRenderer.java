package stellarium.stellars.star;

import org.lwjgl.opengl.GL11;

import net.minecraft.util.ResourceLocation;
import stellarium.api.PerDimensionResource;
import stellarium.render.ICelestialLayerRenderer;
import stellarium.render.StellarRenderInfo;

public class LayerStarRenderer implements ICelestialLayerRenderer {
	
	private static final PerDimensionResource locationStar =
			new PerDimensionResource("Star", new ResourceLocation("stellarium", "stellar/star.png"));

	@Override
	public void preRender(StellarRenderInfo info) {
		GL11.glColor4f(1.0f, 1.0f, 1.0f, info.weathereff);

		info.mc.renderEngine.bindTexture(locationStar.getLocationFor(info.mc.theWorld));
		info.tessellator.startDrawingQuads();
	}

	@Override
	public void postRender(StellarRenderInfo info) {
		info.tessellator.draw();
	}

}
