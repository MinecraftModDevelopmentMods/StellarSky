package stellarium.world;

import net.minecraft.world.World;
import stellarapi.api.ICelestialPack;
import stellarapi.api.ICelestialScene;
import stellarapi.api.lib.config.INBTConfig;
import stellarapi.api.world.worldset.WorldSet;
import stellarium.StellarSky;

public enum StellarPack implements ICelestialPack {
	INSTANCE;

	@Override
	public String getPackName() {
		return "Stellar Sky";
	}

	@Override
	public ICelestialScene getScene(WorldSet worldSet, World world, boolean isDefault) {
		// Load settings
		PerDimensionSettings settings;
		if(isDefault) {
			settings = new PerDimensionSettings(worldSet);
			settings.setPropsAsDefault();
			settings.setFlagsForDefault();
		} else {
			settings = (PerDimensionSettings) ((INBTConfig) StellarSky.PROXY.getDimensionSettings().getSubConfig(worldSet.name)).copy();
		}

		return new StellarScene(world, worldSet, settings);
	}

}
