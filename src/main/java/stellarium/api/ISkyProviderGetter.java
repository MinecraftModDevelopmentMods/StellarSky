package stellarium.api;

import net.minecraft.world.World;

/**
 * Do not use this!
 * */
@Deprecated
public interface ISkyProviderGetter {
	
	public boolean hasSkyProvider(World world);
	public ISkyProvider getSkyProvider(World world);

}
