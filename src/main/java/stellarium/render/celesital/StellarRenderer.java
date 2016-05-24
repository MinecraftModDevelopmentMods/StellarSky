package stellarium.render.celesital;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import stellarapi.api.lib.config.IConfigHandler;
import stellarium.render.atmosphere.IAtmRenderedObjects;
import stellarium.stellars.layer.IRenderCache;
import stellarium.stellars.layer.StellarLayerRegistry;
import stellarium.stellars.layer.StellarObject;
import stellarium.stellars.layer.StellarObjectContainer;

@SideOnly(Side.CLIENT)
public class StellarRenderer {
	
	public void refreshRenderer() {
		StellarRenderingRegistry.getInstance().refresh();
		StellarLayerRegistry.getInstance().registerRenderers();
	}
	
	public void render(StellarRenderInfo info, List<StellarObjectContainer> layers) {
		for(StellarObjectContainer<StellarObject, IConfigHandler> layer : layers) {
			ICelestialLayerRenderer layerRenderer = null;
			
			int rendererId = layer.getType().getLayerRendererIndex();
			if(rendererId != -1)
				layerRenderer = StellarRenderingRegistry.getInstance().getLayerRenderer(rendererId);
			
			if(layerRenderer != null && !layerRenderer.acceptPass(info.pass))
				continue;
			
			if(layerRenderer != null)
				layerRenderer.preRender(info);
						
			for(IRenderCache cache : layer.getRenderCacheList())
			{
				ICelestialObjectRenderer objRenderer = StellarRenderingRegistry.getInstance().getObjectRenderer(cache.getRenderId());
				objRenderer.render(info, cache);
			}
			
			if(layerRenderer != null)
				layerRenderer.postRender(info);
		}
	}

}
