package stellarium.render.sky;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import stellarium.client.ClientSettings;
import stellarium.lib.hierarchy.EnumIDEvaluator;
import stellarium.lib.hierarchy.Hierarchy;
import stellarium.lib.hierarchy.HierarchyCall;
import stellarium.lib.hierarchy.HierarchyDistributor;
import stellarium.lib.hierarchy.HierarchyElement;
import stellarium.render.stellars.StellarModel;
import stellarium.stellars.StellarManager;
import stellarium.view.ViewerInfo;
import stellarium.world.StellarDimensionManager;

@Hierarchy(idEvaluator = "skyrenderable")
public class SkyModel {

	@HierarchyElement(type = StellarModel.class)
	private StellarModel model;

	public static enum EnumSkyRenderable {
		Stellar,
		Display,
		Landscape;
	}

	static {
		HierarchyDistributor.INSTANCE.registerEvaluator("skyrenderable",
				new EnumIDEvaluator(EnumSkyRenderable.class));
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
