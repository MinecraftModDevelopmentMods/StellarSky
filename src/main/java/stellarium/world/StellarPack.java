package stellarium.world;

import net.minecraft.world.World;
import stellarapi.api.ICelestialPack;
import stellarapi.api.ICelestialScene;
import stellarapi.api.world.worldset.WorldSet;

public enum StellarPack implements ICelestialPack {
	INSTANCE;

	@Override
	public String getPackName() {
		return "Stellar Sky";
	}

	@Override
	public ICelestialScene getScene(WorldSet worldSet, World world, boolean vanillaServer) {
		// TODO Adapts to the Stellar API - vanilla check can also be done with it
		// Or does it mean just lack of Stellar API?
		return new StellarScene(world, worldSet);
	}

}
