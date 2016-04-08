package stellarium.stellars.system;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import sciapi.api.value.euclidian.EVector;
import stellarium.render.StellarRenderInfo;
import stellarium.stellars.layer.ICelestialLayerRenderer;

public class LayerSolarSystemRenderer implements ICelestialLayerRenderer {

	@Override
	public void preRender(StellarRenderInfo info) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, info.weathereff);
	}

	@Override
	public void postRender(StellarRenderInfo info) { }


}
