package stellarium.render.sky;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.world.World;
import stellarium.client.ClientSettings;
import stellarium.display.DisplayModel;
import stellarium.lib.hierarchy.EnumIDEvaluator;
import stellarium.lib.hierarchy.Hierarchy;
import stellarium.lib.hierarchy.HierarchyDistributor;
import stellarium.lib.hierarchy.HierarchyElement;
import stellarium.render.stellars.StellarModel;
import stellarium.stellars.StellarManager;
import stellarium.stellars.layer.CelestialManager;
import stellarium.view.ViewerInfo;
import stellarium.world.StellarScene;
import stellarium.world.landscape.LandscapeModel;

@Hierarchy(idEvaluator = "skyrenderable")
public class SkyModel {

	@HierarchyElement(type = StellarModel.class)
	private StellarModel model;
	
	@HierarchyElement(type = DisplayModel.class)
	private DisplayModel displayModel;
	
	@HierarchyElement(type = LandscapeModel.class)
	private LandscapeModel landscapeModel;

	public static enum EnumSkyRenderable {
		Stellar,
		Display,
		Landscape;
	}

	static {
		HierarchyDistributor.INSTANCE.registerEvaluator("skyrenderable",
				new EnumIDEvaluator(EnumSkyRenderable.class));
	}
	
	public SkyModel(CelestialManager clientCelestialManager) {
		if(!OpenGlHelper.isFramebufferEnabled())
			throw new IllegalStateException("FBO must be enabled for Stellar Sky to run.");

		this.model = new StellarModel(clientCelestialManager);
		this.displayModel = new DisplayModel();
		this.landscapeModel = new LandscapeModel();
	}

	public void initializeSettings(ClientSettings settings) {
		model.initializeSettings(settings);
		displayModel.initializeSettings(settings);
		landscapeModel.initializeSettings(settings);
	}

	public void updateSettings(ClientSettings settings) {
		model.updateSettings(settings);
		displayModel.updateSettings(settings);
		landscapeModel.updateSettings(settings);
	}
	
	/**
	 * Called directly after the celestial manager is evaluated,
	 * to initialize the state of render models.
	 * */
	public void stellarLoad(StellarManager manager) {
		model.stellarLoad(manager);
		displayModel.stellarLoad(manager);
	}
	
	/**
	 * Called directly after all the collections are collected,
	 * to initialize the state of render models.
	 * */
	public void dimensionLoad(StellarScene dimManager) {
		model.dimensionLoad(dimManager);
		displayModel.dimensionLoad(dimManager);
		landscapeModel.dimensionLoad(dimManager);
	}
	
	public void onTick(World world, ViewerInfo update) {
		model.onTick(world, update);
		displayModel.onTick(world, update);
		landscapeModel.updateCache();
	}
}
