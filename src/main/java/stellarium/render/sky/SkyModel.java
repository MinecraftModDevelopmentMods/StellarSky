package stellarium.render.sky;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.world.World;
import stellarium.client.ClientSettings;
import stellarium.display.DisplayModel;
import stellarium.render.stellars.StellarModel;
import stellarium.stellars.StellarManager;
import stellarium.stellars.layer.CelestialManager;
import stellarium.view.ViewerInfo;
import stellarium.world.StellarScene;
import stellarium.world.landscape.LandscapeModel;

public class SkyModel {

	final StellarModel stellarModel;
	final DisplayModel displayModel;
	final LandscapeModel landscapeModel;

	public SkyModel(CelestialManager clientCelestialManager) {
		if(!OpenGlHelper.isFramebufferEnabled())
			throw new IllegalStateException("FBO must be enabled for Stellar Sky to run.");

		this.stellarModel = new StellarModel(clientCelestialManager);
		this.displayModel = new DisplayModel();
		this.landscapeModel = new LandscapeModel();
	}

	public void initializeSettings(ClientSettings settings) {
		stellarModel.initializeSettings(settings);
		displayModel.initializeSettings(settings);
		landscapeModel.initializeSettings(settings);
	}

	public void updateSettings(ClientSettings settings) {
		stellarModel.updateSettings(settings);
		displayModel.updateSettings(settings);
		landscapeModel.updateSettings(settings);
	}
	
	/**
	 * Called directly after the celestial manager is evaluated,
	 * to initialize the state of render models.
	 * */
	public void stellarLoad(StellarManager manager) {
		stellarModel.stellarLoad(manager);
		displayModel.stellarLoad(manager);
	}
	
	/**
	 * Called directly after all the collections are collected,
	 * to initialize the state of render models.
	 * */
	public void dimensionLoad(StellarScene dimManager) {
		stellarModel.dimensionLoad(dimManager);
		displayModel.dimensionLoad(dimManager);
		landscapeModel.dimensionLoad(dimManager);
	}
	
	public void onTick(World world, ViewerInfo update) {
		stellarModel.onTick(world, update);
		displayModel.onTick(world, update);
		landscapeModel.updateCache();
	}
}
