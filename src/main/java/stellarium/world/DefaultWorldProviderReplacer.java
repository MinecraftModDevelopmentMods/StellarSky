package stellarium.world;

import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import stellarium.api.IWorldProviderReplacer;

public class DefaultWorldProviderReplacer implements IWorldProviderReplacer {

	@Override
	public boolean accept(World world, WorldProvider provider) {
		return true;
	}

	@Override
	public WorldProvider createWorldProvider(World world, WorldProvider originalProvider) {
		return new StellarWorldProvider(originalProvider);
	}

}
