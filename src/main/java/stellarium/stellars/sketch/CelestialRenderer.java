package stellarium.stellars.sketch;

import java.util.List;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;

@SideOnly(Side.CLIENT)
public class CelestialRenderer {
	
	public void refreshRenderer(List<ICelestialLayer> layers) {
		CelestialRenderingRegistry.getInstance().refresh();
		for(ICelestialLayer layer : layers)
			layer.registerRenderers();
	}
	
	public void render(Minecraft mc, Tessellator tessellator, List<ICelestialLayer> layers, float bglight, float weathereff, float partialTicks) {
		for(ICelestialLayer layer : layers) {
			ICelestialLayerRenderer layerRenderer = CelestialRenderingRegistry.getInstance().getLayerRenderer(layer.getLayerRendererIndex());
			layerRenderer.preRender(mc, tessellator, bglight, weathereff, partialTicks);
			
			for(CelestialObject object : layer.getObjectList())
			{
				if(object.getRenderId() != -1) {
					ICelestialObjectRenderer objRenderer = CelestialRenderingRegistry.getInstance().getObjectRenderer(object.getRenderId());
					objRenderer.render(mc, tessellator, object.getRenderCache(), bglight, weathereff, partialTicks);
				}
			}
			
			layerRenderer.postRender(mc, tessellator, bglight, weathereff, partialTicks);
		}
	}

}
