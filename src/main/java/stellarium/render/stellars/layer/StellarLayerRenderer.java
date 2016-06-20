package stellarium.render.stellars.layer;

import stellarium.lib.render.IGenericRenderer;
import stellarium.render.stellars.access.EnumStellarPass;
import stellarium.stellars.layer.StellarObject;
import stellarium.stellars.render.ICelestialLayerRenderer;
import stellarium.stellars.render.ICelestialObjectRenderer;

public enum StellarLayerRenderer implements IGenericRenderer<Void, EnumStellarPass, StellarLayerModel<StellarObject>, LayerRenderInformation> {
	INSTANCE;

	@Override
	public void initialize(Void settings) { }

	@Override
	public void preRender(Void settings, LayerRenderInformation info) { }

	@Override
	public void renderPass(StellarLayerModel<StellarObject> model, EnumStellarPass pass, LayerRenderInformation info) {
		ICelestialLayerRenderer layerRenderer = model.getLayerType().getLayerRenderer();

		if(!layerRenderer.acceptPass(pass))
			return;
		
		layerRenderer.preRender(info.tessellator, pass);
		
		for(IObjRenderCache cache : model.getRenderCaches()) {
			ICelestialObjectRenderer objRenderer = cache.getRenderer();
			objRenderer.render(cache, pass, info);
		}
		
		if(layerRenderer != null)
			layerRenderer.postRender(info.tessellator, pass);
	}

	@Override
	public void postRender(Void settings, LayerRenderInformation info) { }

}
