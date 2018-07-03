package stellarium;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import stellarium.stellars.StellarManager;
import stellarium.world.StellarScene;

public class StellarTickHandler {
	@SubscribeEvent
	public void tickStart(TickEvent.ClientTickEvent e) {
		if(e.phase == TickEvent.Phase.START){
			World world = StellarSky.PROXY.getDefWorld();
			
			if(world != null) {				
				StellarManager manager = StellarManager.getManager(world);
				if(manager.getCelestialManager() != null) {
					manager.update(world.getWorldTime());
					StellarScene dimManager = StellarScene.getScene(world);
					if(dimManager != null) {
						dimManager.update(world, world.getWorldTime(), world.getTotalWorldTime());
						StellarSky.PROXY.updateTick();
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void tickStart(TickEvent.ServerTickEvent e) {
		if(e.phase == TickEvent.Phase.START) {
			MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
			World world = server.getEntityWorld();

			if(world != null) {
				StellarManager manager = StellarManager.getManager(world);
				
				if(manager.getCelestialManager() != null)
					manager.update(world.getWorldTime());
			}
		}
	}

	@SubscribeEvent
	public void tickStart(TickEvent.WorldTickEvent e) {
		if(e.phase == TickEvent.Phase.START && e.side == Side.SERVER){
			MinecraftServer server = e.world.getMinecraftServer();
			World defWorld = server.getEntityWorld();
			
			StellarScene dimManager = StellarScene.getScene(e.world);
			if(dimManager != null)
				dimManager.update(e.world, e.world.getWorldTime(), defWorld.getTotalWorldTime());
		}
	}
}
