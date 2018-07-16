package stellarium.render.stellars.layer;

import org.apache.commons.lang3.tuple.Pair;

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

		for(Pair<StellarObject, IObjRenderCache> pair : model.getRenderCaches()) {
			IObjRenderCache cache = pair.getRight();
			ICelestialObjectRenderer objRenderer = cache.getRenderer();
			objRenderer.render(cache, pass, info);
		}
		
		if(layerRenderer != null)
			layerRenderer.postRender(pass, info);
	}
}
