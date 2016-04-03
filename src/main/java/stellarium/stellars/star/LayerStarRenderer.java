package stellarium.stellars.star;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import sciapi.api.value.euclidian.EVector;
import stellarium.stellars.layer.ICelestialLayerRenderer;

public class LayerStarRenderer implements ICelestialLayerRenderer {
	
	private static final ResourceLocation locationStarPng = new ResourceLocation("stellarium", "stellar/star.png");

	@Override
	public void preRender(Minecraft mc, Tessellator tessellator, float bglight, float weathereff, float partialTicks) {
		mc.renderEngine.bindTexture(locationStarPng);
		tessellator.startDrawingQuads();
	}

	@Override
	public void postRender(Minecraft mc, Tessellator tessellator, float bglight, float weathereff, float partialTicks) {
		tessellator.draw();
	}

}
