package stellarium.stellars.system;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import sciapi.api.value.euclidian.EVector;
import stellarium.stellars.layer.ICelestialLayerRenderer;

public class LayerSolarSystemRenderer implements ICelestialLayerRenderer {
	
	@Override
	public void preRender(Minecraft mc, Tessellator tessellator, float bglight, float weathereff, float partialTicks) { }

	@Override
	public void postRender(Minecraft mc, Tessellator tessellator, float bglight, float weathereff, float partialTicks) { }

}
