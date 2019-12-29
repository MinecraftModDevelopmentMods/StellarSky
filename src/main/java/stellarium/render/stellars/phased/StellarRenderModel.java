package stellarium.render.stellars.phased;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.world.World;
import stellarium.client.ClientSettings;
import stellarium.render.stellars.access.ICheckedAtmModel;
import stellarium.render.stellars.layer.StellarLayerModel;
import stellarium.stellars.StellarManager;
import stellarium.stellars.layer.CelestialManager;
import stellarium.stellars.layer.StellarLayerRegistry;
import stellarium.stellars.layer.StellarCollection;
import stellarium.view.ViewerInfo;
import stellarium.world.StellarScene;

public class StellarRenderModel {
	//Models for client-only layered objects.
	private List<StellarLayerModel> baseModels = Lists.newArrayList();

	//Models for in-game objects.
	List<StellarLayerModel> layerModels = Lists.newArrayList();

	public StellarRenderModel(CelestialManager celManager, ICheckedAtmModel atmModel) {
		for(StellarCollection layer : celManager.getLayers()) {
			StellarLayerModel layerModel = new StellarLayerModel(layer);
			baseModels.add(layerModel);
		}
	}

	public void updateSettings(ClientSettings settings) {
		for(StellarLayerModel<?> model : this.layerModels)
			model.updateSettings(settings);
	}

	public void onStellarLoad(StellarManager manager) {
		layerModels.clear();
		for(int i = 0; i < baseModels.size(); i++) {
			CelestialManager celestialWorld = manager.getCelestialManager();
			layerModels.add(baseModels.get(i).copy(celestialWorld.getLayers().get(i)));
		}
	}

	public void onTick(World world, ViewerInfo update) {
		for(StellarLayerModel model : this.layerModels)
			model.onStellarTick(update);
	}

}
