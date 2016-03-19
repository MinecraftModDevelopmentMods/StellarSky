package stellarium.common;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import stellarium.StellarSky;
import stellarium.api.ISkyProvider;
import stellarium.api.ISkyProviderGetter;
import stellarium.stellars.StellarManager;

public class SkyProviderGetter implements ISkyProviderGetter {

	@Override
	public ISkyProvider getSkyProvider() {
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		World world;
		
		if(side.isClient())
			world = StellarSky.proxy.getDefWorld();
		else world = MinecraftServer.getServer().worldServers[0];
		
		return StellarManager.getManager(world);
	}

}
