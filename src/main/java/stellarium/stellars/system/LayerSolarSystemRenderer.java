package stellarium.stellars.system;

import net.minecraft.client.renderer.GlStateManager;
import stellarium.render.ICelestialLayerRenderer;
import stellarium.render.StellarRenderInfo;

public class LayerSolarSystemRenderer implements ICelestialLayerRenderer {

	@Override
	public void preRender(StellarRenderInfo info) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, info.weathereff);
	}

	@Override
	public void postRender(StellarRenderInfo info) { }
	
}
