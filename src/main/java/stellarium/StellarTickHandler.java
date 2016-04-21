package stellarium;

import java.lang.reflect.Field;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
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
		if(e.phase == Phase.START){
			World world = StellarSky.proxy.getDefWorld(true);
			
			if(world != null) {
				StellarManager manager = StellarManager.getManager(true);
				if(manager.getCelestialManager() != null) {
					manager.update(world.getWorldTime());
					StellarDimensionManager dimManager = StellarDimensionManager.get(world);
					if(dimManager != null)
					{
						dimManager.update(world, world.getWorldTime());
						manager.updateClient(StellarSky.proxy.getClientSettings());
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void tickStart(TickEvent.ServerTickEvent e) {
		if(e.phase == Phase.START){
			World world = StellarSky.proxy.getDefWorld(false);
			
			if(world != null) {
				StellarManager manager = StellarManager.getManager(false);
				
				if(manager.getSettings().serverEnabled)
					manager.update(world.getWorldTime());
			}
		}
	}
	
	@SubscribeEvent
	public void tickStart(TickEvent.WorldTickEvent e) {
		if(e.phase == Phase.START){
			StellarDimensionManager dimManager = StellarDimensionManager.get(e.world);
			if(dimManager != null)
				dimManager.update(e.world, e.world.getWorldTime());
		}
	}

}
