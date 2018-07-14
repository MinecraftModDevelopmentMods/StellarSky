package stellarium.render.stellars;

import net.minecraft.world.World;
import stellarium.client.ClientSettings;
import stellarium.render.stellars.atmosphere.AtmosphereModel;
import stellarium.render.stellars.phased.StellarRenderModel;
import stellarium.stellars.StellarManager;
import stellarium.stellars.layer.CelestialManager;
import stellarium.view.ViewerInfo;
import stellarium.world.StellarScene;

public class StellarModel {
	final StellarRenderModel layersModel;
	final AtmosphereModel atmModel;

	public StellarModel(CelestialManager clientCelestialManager) {
		this.atmModel = new AtmosphereModel();
		this.layersModel = new StellarRenderModel(clientCelestialManager, this.atmModel);
	}

	public void initializeSettings(ClientSettings settings) {
		settings.putSubConfig(QualitySettings.KEY, new QualitySettings());
		atmModel.initializeSettings(settings);
		layersModel.initializeSettings(settings);
	}

	public void updateSettings(ClientSettings settings) {
		layersModel.updateSettings(settings);
	}

	public void stellarLoad(StellarManager manager) {
		layersModel.onStellarLoad(manager);
	}

	public void dimensionLoad(StellarScene dimManager) {
		atmModel.dimensionLoad(dimManager);
	}

	public void onTick(World world, ViewerInfo update) {
		layersModel.onTick(world, update);
		atmModel.onTick(world, update);
	}
}
