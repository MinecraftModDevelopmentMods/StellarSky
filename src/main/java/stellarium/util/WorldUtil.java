package stellarium.util;

import net.minecraft.world.World;

public class WorldUtil {
	
	public static String getWorldName(World world) {
		if(world.provider.dimensionId == 0)
			return "Overworld";

		else return world.provider.getDimensionName();
	}

}
