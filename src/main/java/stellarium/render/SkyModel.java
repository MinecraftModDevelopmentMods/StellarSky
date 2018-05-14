package stellarium.render;

import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

import net.minecraft.world.World;
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

	public SkyModel(CelestialManager clientCelestialManager) {
		if(!OpenGlUtil.FRAMEBUFFER_SUPPORTED)
			System.err.println("FBO isn't supported, Stellar Sky can't operate in this case.");

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
