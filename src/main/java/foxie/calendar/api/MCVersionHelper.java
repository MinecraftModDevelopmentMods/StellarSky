package foxie.calendar.api;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;

public class MCVersionHelper {
   public static int getDimensionId(WorldProvider provider) {
      return provider.dimensionId;
   }

   public static int getDimensionId(World world) {
      return getDimensionId(world.provider);
   }

   public static void log(String msg) {
      FMLLog.info(msg);
   }

   public static String getCurrentModId() {
      return Loader.instance().activeModContainer().getModId();
   }
}
