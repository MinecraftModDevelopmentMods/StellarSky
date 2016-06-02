package stellarium.render.stellars.phased;

import java.util.List;

import com.google.common.collect.Lists;

import stellarium.client.ClientSettings;
import stellarium.lib.hierarchy.EnumCallOrder;
import stellarium.lib.hierarchy.Hierarchy;
import stellarium.lib.hierarchy.HierarchyCall;
import stellarium.lib.hierarchy.HierarchyDistributor;
import stellarium.lib.hierarchy.HierarchyElement;
import stellarium.render.stellars.access.IStellarChecker;
import stellarium.render.stellars.layer.StellarLayerModel;
import stellarium.stellars.StellarManager;
import stellarium.stellars.layer.CelestialManager;
import stellarium.stellars.layer.StellarObjectContainer;
import stellarium.view.ViewerInfo;
import stellarium.world.StellarDimensionManager;

@Hierarchy
public class StellarRenderModel {
	@HierarchyElement(type = StellarLayerModel.class)
	private List<StellarLayerModel> currentModels;

	//Models for client-only layered objects.
	private List<StellarLayerModel> baseModels = Lists.newArrayList();

	//Models for in-game objects.
	private List<StellarLayerModel> layerModels = Lists.newArrayList();

	private StellarChecker checker = new StellarChecker();

	public StellarRenderModel(CelestialManager celManager) {
		for(StellarObjectContainer layer : celManager.getLayers()) {
			StellarLayerModel layerModel = new StellarLayerModel(layer);
			layer.bindRenderModel(layerModel);
			baseModels.add(layerModel);
		}

		this.currentModels = this.baseModels;
	}

	@HierarchyCall(id = "updateClientSettings")
	public void updateSettings(ClientSettings settings) { }
	
	@HierarchyCall(id = "stellarLoad", callOrder = EnumCallOrder.Custom)
	public void onStellarLoad(StellarManager manager) {
		layerModels.clear();
		for(int i = 0; i < baseModels.size(); i++) {
			CelestialManager managerForWorld = manager.getCelestialManager();
			layerModels.add(baseModels.get(i).copy(managerForWorld.getLayers().get(i)));
		}

		this.currentModels = this.layerModels;
	}
	
	@HierarchyCall(id = "dimensionLoad", callOrder = EnumCallOrder.Custom)
	public void onDimensionLoad(StellarDimensionManager dimManager) {
		for(int i = 0; i < layerModels.size(); i++)
			layerModels.get(i).onLoadCollection(dimManager.getCollections().get(i));
	}

	@HierarchyCall(id = "onSkyTick", callOrder = EnumCallOrder.Custom,
			acceptParams = @HierarchyCall.Accept(ViewerInfo.class))
	public void onTick(ViewerInfo update) {
		checker.setView(update);
		HierarchyDistributor.INSTANCE.call(this, "onStellarTick", update, this.checker);
	}
	
	@HierarchyCall(id = "onStellarTick")
	public void onStellarTick(ViewerInfo update, IStellarChecker checker) { }

}
