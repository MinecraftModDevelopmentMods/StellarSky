package stellarium.render.stellars;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import stellarium.client.ClientSettings;
import stellarium.lib.hierarchy.EnumIDEvaluator;
import stellarium.lib.hierarchy.Hierarchy;
import stellarium.lib.hierarchy.HierarchyCall;
import stellarium.lib.hierarchy.HierarchyDistributor;
import stellarium.lib.hierarchy.HierarchyElement;
import stellarium.render.stellars.atmosphere.AtmosphereModel;
import stellarium.render.stellars.phased.StellarRenderModel;
import stellarium.stellars.StellarManager;
import stellarium.view.ViewerInfo;
import stellarium.world.StellarDimensionManager;

@Hierarchy(idEvaluator = "stellarrenderable")
public class StellarModel {

	@HierarchyElement(type = StellarRenderModel.class)
	private StellarRenderModel layersModel;

	@HierarchyElement(type = AtmosphereModel.class)
	private AtmosphereModel atmModel;

	public static enum EnumStellarRenderable {
		Stellar,
		Atmosphere,
	}

	static {
		HierarchyDistributor.INSTANCE.registerEvaluator("stellarrenderable",
				new EnumIDEvaluator(EnumStellarRenderable.class));
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
