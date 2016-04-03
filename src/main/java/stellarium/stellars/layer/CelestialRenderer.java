package stellarium.stellars.layer;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import stellarium.config.IConfigHandler;

@SideOnly(Side.CLIENT)
public class CelestialRenderer {
	
	public void refreshRenderer(List<ICelestialLayer> layers) {
		CelestialRenderingRegistry.getInstance().refresh();
		for(ICelestialLayer layer : layers)
			layer.registerRenderers();
	}
	
	public void render(Minecraft mc, Tessellator tessellator, List<ICelestialLayer> layers, float bglight, float weathereff, float partialTicks) {
		for(ICelestialLayer<? extends IConfigHandler> layer : layers) {
			ICelestialLayerRenderer layerRenderer = null;
			
			if(layer.getLayerRendererIndex() != -1)
				layerRenderer = CelestialRenderingRegistry.getInstance().getLayerRenderer(layer.getLayerRendererIndex());
			
			if(layerRenderer != null)
				layerRenderer.preRender(mc, tessellator, bglight, weathereff, partialTicks);
			
			for(CelestialObject object : layer.getObjectList())
			{
				if(object.getRenderId() != -1) {
					ICelestialObjectRenderer objRenderer = CelestialRenderingRegistry.getInstance().getObjectRenderer(object.getRenderId());
					objRenderer.render(mc, tessellator, object.getRenderCache(), bglight, weathereff, partialTicks);
				}
			}
			
			if(layerRenderer != null)
				layerRenderer.postRender(mc, tessellator, bglight, weathereff, partialTicks);
		}
	}

}
