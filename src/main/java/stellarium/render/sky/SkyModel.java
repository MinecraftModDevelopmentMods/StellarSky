package stellarium.render.sky;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import stellarium.client.ClientSettings;
import stellarium.display.DisplayModel;
import stellarium.lib.hierarchy.EnumIDEvaluator;
import stellarium.lib.hierarchy.Hierarchy;
import stellarium.lib.hierarchy.HierarchyCall;
import stellarium.lib.hierarchy.HierarchyDistributor;
import stellarium.lib.hierarchy.HierarchyElement;
import stellarium.render.stellars.StellarModel;
import stellarium.stellars.StellarManager;
import stellarium.stellars.layer.CelestialManager;
import stellarium.view.ViewerInfo;
import stellarium.world.StellarDimensionManager;
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
		this.model = new StellarModel(clientCelestialManager);
		this.displayModel = new DisplayModel();
		this.landscapeModel = new LandscapeModel();
	}

	@HierarchyCall(id = "initializeClientSettings")
	public void initializeSettings(ClientSettings settings) { }

	@HierarchyCall(id = "updateClientSettings")
	public void updateSettings(ClientSettings settings) { }
	
	@HierarchyCall(id = "stellarLoad")
	public void stellarLoad(StellarManager manager) { }
	
	@HierarchyCall(id = "dimensionLoad")
	public void dimensionLoad(StellarDimensionManager dimManager) { }
	
	@HierarchyCall(id = "onSkyTick")
	public void onTick(World world, ViewerInfo update) { }
}
