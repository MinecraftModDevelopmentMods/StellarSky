package stellarium.render.stellars.atmosphere;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import stellarapi.api.lib.math.Spmath;
import stellarium.client.ClientSettings;
import stellarium.lib.hierarchy.Hierarchy;
import stellarium.lib.hierarchy.HierarchyCall;
import stellarium.view.ViewerInfo;
import stellarium.world.StellarDimensionManager;

@Hierarchy
public class AtmosphereModel {

	private float outerRadius = 820.0f;
	private float innerRadius = 800.0f;

	private float height;
	private float skyred, skygreen, skyblue;
	private float skyExtRed, skyExtGreen, skyExtBlue;

	@HierarchyCall(id = "initializeClientSettings")
	public void initializeSettings(ClientSettings settings) {
		settings.putSubConfig(AtmosphereSettings.KEY, new AtmosphereSettings());
	}

	@HierarchyCall(id = "dimensionLoad")
	public void dimensionLoad(StellarDimensionManager dimManager) {
		// TODO per-dimension atmosphere
		;
	}

	@HierarchyCall(id = "onSkyTick")
	public void onTick(World world, ViewerInfo update) {
		int i = MathHelper.floor_double(update.currentPosition.getX());
		int j = MathHelper.floor_double(update.currentPosition.getY());
		int k = MathHelper.floor_double(update.currentPosition.getZ());
		int l = ForgeHooksClient.getSkyBlendColour(world, i, j, k);
		this.skyred = (float)(l >> 16 & 255) / 255.0F;
		this.skygreen = (float)(l >> 8 & 255) / 255.0F;
		this.skyblue = (float)(l & 255) / 255.0F;
		
		this.height = (float)(update.currentPosition.getY() - world.getHorizon()) / world.getHeight();
	}
}
