package stellarium.render.stellars.phased;

import java.util.List;

import com.google.common.collect.Lists;

import stellarium.lib.render.IRenderModel;
import stellarium.render.stellars.AtmStellarUpdateInfo;
import stellarium.render.stellars.layer.LayerSettings;
import stellarium.render.stellars.layer.LayerUpdateInfo;
import stellarium.render.stellars.layer.StellarLayerModel;
import stellarium.stellars.layer.CelestialManager;
import stellarium.stellars.layer.StellarObjectContainer;

public class StellarRenderModel implements IRenderModel<StellarSettings, AtmStellarUpdateInfo> {
	
	//Models for client-only layered objects.
	private List<StellarLayerModel> baseModels = Lists.newArrayList();
	
	//Models for in-game objects.
	private List<StellarLayerModel> layerModels = Lists.newArrayList();

	public List<StellarLayerModel> getLayerModels() {
		return this.layerModels;
	}
	
	private StellarChecker checker = new StellarChecker();

	public StellarRenderModel(CelestialManager celManager) {
		for(StellarObjectContainer layer : celManager.getLayers()) {
			StellarLayerModel layerModel = new StellarLayerModel(layer);
			layer.bindRenderModel(layerModel);
			baseModels.add(layerModel);
		}
	}

	@Override
	public void initialize(StellarSettings settings) {
		for(StellarLayerModel model : this.baseModels)
			model.initialize(new LayerSettings(settings.getSubConfig(model.getConfigName())));
	}

	@Override
	public void update(StellarSettings settings, AtmStellarUpdateInfo update) {
		checker.setView(update.viewer);
		
		LayerUpdateInfo layerUpdate = new LayerUpdateInfo(update); 
		for(StellarLayerModel model : this.layerModels)
			model.update(new LayerSettings(settings.getSubConfig(model.getConfigName())), layerUpdate);
	}

}
