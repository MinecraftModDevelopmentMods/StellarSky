package stellarium.common;

import net.minecraft.world.World;
import stellarium.api.ISkyProvider;
import stellarium.api.ISkyProviderGetter;
import stellarium.world.StellarWorldProvider;

public class SkyProviderGetter implements ISkyProviderGetter {

	@Override
	public boolean hasSkyProvider(World world) {
		return world.provider instanceof StellarWorldProvider;
	}

	@Override
	public ISkyProvider getSkyProvider(World world) {
		if(this.hasSkyProvider(world))
			return (ISkyProvider) world.provider;
		else return null;
	}

}
