package stellarium.stellars.layer;

import stellarapi.api.lib.config.IConfigHandler;
import stellarium.client.ClientSettings;
import stellarium.render.atmosphere.IAtmRenderedObjects;
import stellarium.render.atmosphere.IAtmosphericChecker;
import stellarium.render.atmosphere.IAtmosphericTessellator;
import stellarium.render.atmosphere.ViewerInfo;
import stellarium.stellars.render.ICelestialLayerRenderer;
import stellarium.stellars.render.ICelestialObjectRenderer;
import stellarium.stellars.render.StellarRenderingRegistry;

public class StellarLayerRenderHandler implements IAtmRenderedObjects {
	
	private CelestialManager celManager;
	private ClientSettings settings;
	
	public StellarLayerRenderHandler(CelestialManager celManager, ClientSettings settings) {
		this.celManager = celManager;
		this.settings = settings;
	}

	@Override
	public void check(ViewerInfo info, IAtmosphericChecker checker) {
		for(StellarObjectContainer layer : celManager.getLayers()) {
			String layerName = layer.getConfigName();
			layer.updateClient(this.settings, layerName != null? settings.getSubConfig(layerName) : null, info, checker);
		}
	}

	@Override
	public void render(IAtmosphericTessellator tessellator, boolean forOpaque, boolean hasTexture) {
		for(StellarObjectContainer<StellarObject, IConfigHandler> layer : celManager.getLayers()) {
			ICelestialLayerRenderer layerRenderer = null;
			
			int rendererId = layer.getType().getLayerRendererIndex();
			if(rendererId != -1)
				layerRenderer = StellarRenderingRegistry.getInstance().getLayerRenderer(rendererId);
			
			if(layerRenderer != null && !layerRenderer.acceptPass(forOpaque, hasTexture))
				continue;
			
			if(layerRenderer != null)
				layerRenderer.preRender(tessellator, forOpaque, hasTexture);
			
			for(IRenderCache cache : layer.getRenderCacheList())
			{
				ICelestialObjectRenderer objRenderer = StellarRenderingRegistry.getInstance().getObjectRenderer(cache.getRenderId());
				objRenderer.render(tessellator, cache);
			}
			
			if(layerRenderer != null)
				layerRenderer.postRender(tessellator, forOpaque, hasTexture);
		}
	}

}
