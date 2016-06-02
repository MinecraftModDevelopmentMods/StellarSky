package stellarium.render.stellars.phased;

import stellarium.lib.render.IGenericRenderer;
import stellarium.render.stellars.access.EnumStellarPass;
import stellarium.render.stellars.layer.LayerRenderInformation;
import stellarium.render.stellars.layer.StellarLayerModel;
import stellarium.render.stellars.layer.StellarLayerRenderer;

public class StellarPhasedRenderer implements IGenericRenderer<Void, EnumStellarPass, StellarRenderModel, StellarRenderInformation> {

	private StellarTessellator tessellator = new StellarTessellator();
	private StellarLayerRenderer layerRenderer = new StellarLayerRenderer();
	
	@Override
	public void initialize(Void settings) {
		// TODO Initialization?
	}

	@Override
	public void preRender(Void settings, StellarRenderInformation info) { }

	@Override
	public void renderPass(StellarRenderModel model, EnumStellarPass pass, StellarRenderInformation info) {
		tessellator.initialize(pass, info);
		
		LayerRenderInformation layerInfo = new LayerRenderInformation(info, this.tessellator);
		for(StellarLayerModel layerModel : model.getLayerModels())
			layerRenderer.renderPass(layerModel, pass, layerInfo);
	}

	@Override
	public void postRender(Void settings, StellarRenderInformation info) { }

}
