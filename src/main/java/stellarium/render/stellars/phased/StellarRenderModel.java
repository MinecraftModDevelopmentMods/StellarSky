package stellarium.render.stellars.phased;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.world.World;
import stellarium.client.ClientSettings;
import stellarium.lib.hierarchy.Hierarchy;
import stellarium.lib.hierarchy.HierarchyElement;
import stellarium.render.stellars.layer.StellarLayerModel;
import stellarium.stellars.StellarManager;
import stellarium.stellars.layer.CelestialManager;
import stellarium.stellars.layer.StellarObjectContainer;
import stellarium.view.ViewerInfo;
import stellarium.world.StellarDimensionManager;

@Hierarchy
public class StellarRenderModel {

	//Models for client-only layered objects.
	private List<StellarLayerModel> baseModels = Lists.newArrayList();

	//Models for in-game objects.
	@HierarchyElement(type = StellarLayerModel.class)
	private List<StellarLayerModel> layerModels = Lists.newArrayList();

	private StellarChecker checker = new StellarChecker();

	public StellarRenderModel(CelestialManager celManager) {
		for(StellarObjectContainer layer : celManager.getLayers()) {
			StellarLayerModel layerModel = new StellarLayerModel(layer);
			layer.bindRenderModel(layerModel);
			baseModels.add(layerModel);
		}
	}

	public void updateSettings(ClientSettings settings) {
		checker.setMagLimit(settings.mag_Limit);
		for(StellarLayerModel model : this.layerModels)
			model.updateSettings(settings);
	}
	
	public void onStellarLoad(StellarManager manager) {
		layerModels.clear();
		for(int i = 0; i < baseModels.size(); i++) {
			CelestialManager managerForWorld = manager.getCelestialManager();
			layerModels.add(baseModels.get(i).copy(managerForWorld.getLayers().get(i)));
		}
	}
	
	public void onDimensionLoad(StellarDimensionManager dimManager) {
		for(int i = 0; i < layerModels.size(); i++)
			layerModels.get(i).onLoadCollection(dimManager.getCollections().get(i));
	}

	public void onTick(World world, ViewerInfo update) {
		checker.setView(world, update);
		for(StellarLayerModel model : this.layerModels)
			model.onStellarTick(update, this.checker);
	}

}
