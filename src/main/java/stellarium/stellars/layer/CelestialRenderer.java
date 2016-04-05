package stellarium.stellars.layer;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stellarium.config.IConfigHandler;
import stellarium.render.StellarRenderInfo;

@SideOnly(Side.CLIENT)
public class CelestialRenderer {
	
	public void refreshRenderer(List<ICelestialLayer> layers) {
		CelestialRenderingRegistry.getInstance().refresh();
		for(ICelestialLayer layer : layers)
			layer.registerRenderers();
	}
	
	public void render(List<ICelestialLayer> layers, StellarRenderInfo info) {
		for(ICelestialLayer<? extends IConfigHandler> layer : layers) {
			ICelestialLayerRenderer layerRenderer = null;
			
			if(layer.getLayerRendererIndex() != -1)
				layerRenderer = CelestialRenderingRegistry.getInstance().getLayerRenderer(layer.getLayerRendererIndex());
			
			if(layerRenderer != null)
				layerRenderer.preRender(info);
			
			for(CelestialObject object : layer.getObjectList())
			{
				if(object.getRenderId() != -1) {
					ICelestialObjectRenderer objRenderer = CelestialRenderingRegistry.getInstance().getObjectRenderer(object.getRenderId());
					objRenderer.render(info, object.getRenderCache());
				}
			}
			
			if(layerRenderer != null)
				layerRenderer.postRender(info);
		}
	}

}