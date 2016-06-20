package stellarium;

import java.lang.reflect.Field;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import stellarium.stellars.StellarManager;
import stellarium.world.StellarDimensionManager;

public class StellarTickHandler {
	
	private Field sleep;
			
	public StellarTickHandler() {
		sleep = getField(WorldServer.class, "allPlayersSleeping", "field_73068_P");
	}
	
	public static Field getField(Class<?> clazz, String... fieldNames) {
		return ReflectionHelper.findField(clazz, ObfuscationReflectionHelper.remapFieldNames(clazz.getName(), fieldNames));
	}
	
	@SubscribeEvent
	public void tickStart(TickEvent.ClientTickEvent e) {
		if(e.phase == TickEvent.Phase.START){
			World world = StellarSky.proxy.getDefWorld();
			
			if(world != null) {				
				StellarManager manager = StellarManager.getClientManager();
				if(manager.getCelestialManager() != null) {
					manager.update(world.getWorldTime());
					StellarDimensionManager dimManager = StellarDimensionManager.get(world);
					if(dimManager != null) {
						dimManager.update(world, world.getWorldTime(), world.getTotalWorldTime());
						StellarSky.proxy.updateTick();
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void tickStart(TickEvent.ServerTickEvent e) {
		if(e.phase == TickEvent.Phase.START) {
			// TODO fix this mess; Should not get minecraft server instance from there
			MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
			World world = server.getEntityWorld();
			
			if(world != null) {
				StellarManager manager = StellarManager.getServerManager(server);
				
				if(manager.getSettings().serverEnabled)
					manager.update(world.getWorldTime());
			}
		}
	}
	
	@SubscribeEvent
	public void tickStart(TickEvent.WorldTickEvent e) {
		if(e.phase == TickEvent.Phase.START && e.side == Side.SERVER){
			MinecraftServer server = e.world.getMinecraftServer();
			World defWorld = server.getEntityWorld();
			
			StellarDimensionManager dimManager = StellarDimensionManager.get(e.world);
			if(dimManager != null)
				dimManager.update(e.world, e.world.getWorldTime(), defWorld.getTotalWorldTime());
		}
	}
}
