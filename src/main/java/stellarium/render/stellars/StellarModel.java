package stellarium.render.stellars;

import net.minecraft.world.World;
import stellarium.client.ClientSettings;
import stellarium.lib.hierarchy.EnumIDEvaluator;
import stellarium.lib.hierarchy.Hierarchy;
import stellarium.lib.hierarchy.HierarchyDistributor;
import stellarium.lib.hierarchy.HierarchyElement;
import stellarium.render.stellars.atmosphere.AtmosphereModel;
import stellarium.render.stellars.phased.StellarRenderModel;
import stellarium.stellars.StellarManager;
import stellarium.stellars.layer.CelestialManager;
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
	
	public StellarModel(CelestialManager clientCelestialManager) {
		this.layersModel = new StellarRenderModel(clientCelestialManager);
		this.atmModel = new AtmosphereModel();
	}

	public void initializeSettings(ClientSettings settings) {
		atmModel.initializeSettings(settings);
	}

	public void updateSettings(ClientSettings settings) {
		layersModel.updateSettings(settings);
	}

	public void stellarLoad(StellarManager manager) {
		layersModel.onStellarLoad(manager);
	}

	public void dimensionLoad(StellarDimensionManager dimManager) {
		layersModel.onDimensionLoad(dimManager);
		atmModel.dimensionLoad(dimManager);
	}

	public void onTick(World world, ViewerInfo update) {
		layersModel.onTick(world, update);
		atmModel.onTick(world, update);
	}
}
