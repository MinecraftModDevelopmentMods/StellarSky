package stellarium.render.stellars.layer;

import stellarium.render.stellars.access.EnumStellarPass;
import stellarium.stellars.layer.StellarObject;
import stellarium.stellars.render.ICelestialLayerRenderer;
import stellarium.stellars.render.ICelestialObjectRenderer;

public enum StellarLayerRenderer {
	INSTANCE;

	public void render(StellarLayerModel<StellarObject> model, EnumStellarPass pass, LayerRHelper info) {
		ICelestialLayerRenderer layerRenderer = model.getLayerType().getLayerRenderer();

		if(!layerRenderer.acceptPass(pass))
			return;

		layerRenderer.preRender(pass, info);

		for(IObjRenderCache cache : model.getRenderCaches()) {
			ICelestialObjectRenderer objRenderer = cache.getRenderer();
			objRenderer.render(cache, pass, info);
		}
		
		if(layerRenderer != null)
			layerRenderer.postRender(pass, info);
	}
}
