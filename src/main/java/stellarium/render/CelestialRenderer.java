package stellarium.render;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import stellarium.config.IConfigHandler;
import stellarium.stellars.layer.CelestialObject;
import stellarium.stellars.layer.ICelestialLayer;

@SideOnly(Side.CLIENT)
public class CelestialRenderer {
	
	public void refreshRenderer(List<ICelestialLayer> layers) {
		CelestialRenderingRegistry.getInstance().refresh();
		for(ICelestialLayer layer : layers)
			layer.registerRenderers();
	}
	
	public void render(StellarRenderInfo info, List<ICelestialLayer> layers) {
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
