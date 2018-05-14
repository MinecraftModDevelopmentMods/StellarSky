package stellarium.render.stellars.phased;

import stellarium.render.stellars.access.EnumStellarPass;
import stellarium.render.stellars.layer.LayerRI;
import stellarium.render.stellars.layer.StellarLayerModel;
import stellarium.render.stellars.layer.StellarLayerRenderer;

public enum StellarPhasedRenderer {
	INSTANCE;

	public void render(StellarRenderModel model, EnumStellarPass pass, LayerRI info) {
		info.initialize(pass);
		// Render all layers
		for(StellarLayerModel layerModel : model.layerModels)
			StellarLayerRenderer.INSTANCE.render(layerModel, pass, info);
	}
}
