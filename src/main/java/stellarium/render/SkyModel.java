package stellarium.render;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import stellarapi.api.SAPICapabilities;
import stellarapi.api.view.IAtmosphereEffect;
import stellarapi.api.view.ICCoordinates;
import stellarapi.api.world.ICelestialWorld;
import stellarium.client.ClientSettings;
import stellarium.display.DisplayModel;
import stellarium.render.stellars.StellarModel;
import stellarium.stellars.StellarManager;
import stellarium.stellars.layer.CelestialManager;
import stellarium.util.OpenGlUtil;
import stellarium.view.ViewerInfo;
import stellarium.world.StellarScene;
import stellarium.world.landscape.LandscapeModel;

public class SkyModel {

	final StellarModel stellarModel;
	final DisplayModel displayModel;
	final LandscapeModel landscapeModel;
	final World world;
	private StellarScene dimManager;

	public SkyModel(CelestialManager clientCelestialManager, World world) {
		if(!OpenGlUtil.FRAMEBUFFER_SUPPORTED)
			System.err.println("FBO isn't supported, Stellar Sky can't operate in this case.");

		this.stellarModel = new StellarModel(clientCelestialManager);
		this.displayModel = new DisplayModel();
		this.landscapeModel = new LandscapeModel();
		this.world = world;
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
		this.dimManager = dimManager;
		stellarModel.dimensionLoad(dimManager);
		displayModel.dimensionLoad(dimManager);
		landscapeModel.dimensionLoad(dimManager);
	}
	
	public void onTick(Entity viewer) {
		if(dimManager != null) {
			ICelestialWorld cWorld = world.getCapability(SAPICapabilities.CELESTIAL_CAPABILITY, null);
			ICCoordinates coordinate = cWorld.getCoordinate();
			IAtmosphereEffect sky = cWorld.getSkyEffect();
			ViewerInfo update = new ViewerInfo(coordinate, sky, viewer, 0.0f);

			dimManager.update(world, world.getWorldTime(), world.getTotalWorldTime());
			stellarModel.onTick(world, update);
			displayModel.onTick(world, update);
			landscapeModel.updateCache();
		}
	}
}
