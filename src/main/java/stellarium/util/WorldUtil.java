package stellarium.util;

import net.minecraft.world.World;

public class WorldUtil {
	
	public static String getWorldName(World world) {
		if(world.provider.getDimension() == 0)
			return "Overworld";

		else return world.provider.getDimensionType().getName();
	}

}
