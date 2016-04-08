package stellarium.stellars.system;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import sciapi.api.value.euclidian.EVector;
import stellarium.stellars.layer.ICelestialLayerRenderer;

public class LayerSolarSystemRenderer implements ICelestialLayerRenderer {
	
	@Override
	public void preRender(Minecraft mc, Tessellator tessellator, float bglight, float weathereff, float partialTicks) {
		GL11.glColor4f(1.0f, 1.0f, 1.0f, weathereff);
	}

	@Override
	public void postRender(Minecraft mc, Tessellator tessellator, float bglight, float weathereff, float partialTicks) { }

}
