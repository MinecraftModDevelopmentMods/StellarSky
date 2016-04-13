package stellarium;

import java.lang.reflect.Field;
import java.util.Iterator;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.WorldInfo;
import stellarium.api.ISkyProvider;
import stellarium.api.StellarSkyAPI;
import stellarium.stellars.StellarManager;
import stellarium.stellars.view.StellarDimensionManager;

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
						manager.updateClient(StellarSky.proxy.getClientSettings(),
								dimManager.getViewpoint());
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
			if(e.world != null) {
				StellarDimensionManager dimManager = StellarDimensionManager.get(e.world);
				if(dimManager != null)
					dimManager.update(e.world, e.world.getWorldTime());
				
				StellarManager manager = StellarManager.getManager(false);
				if(!StellarSkyAPI.hasSkyProvider(e.world))
					return;
				
				if(StellarSky.proxy.wakeManager.isEnabled()) {
					WorldServer world = (WorldServer) e.world;

					world.updateAllPlayersSleepingFlag();
					if (world.areAllPlayersAsleep())
						this.tryWakePlayers(world, StellarSkyAPI.getSkyProvider(e.world));

					try {
						sleep.setBoolean(world, false);
					} catch (IllegalArgumentException ex) {
						ex.printStackTrace();
					} catch (IllegalAccessException ex) {
						ex.printStackTrace();
					}
				}
			}
		}
	}

	private void tryWakePlayers(WorldServer world, ISkyProvider skyProvider) {		
        if (world.getGameRules().getGameRuleBooleanValue("doDaylightCycle"))
        {
        	WorldInfo info = world.getWorldInfo();
        	long worldTime = info.getWorldTime();
            info.setWorldTime(StellarSky.proxy.wakeManager.getWakeTime(world, skyProvider, worldTime));
        }

        Iterator iterator = world.playerEntities.iterator();

        while (iterator.hasNext())
        {
            EntityPlayer entityplayer = (EntityPlayer)iterator.next();

            if (entityplayer.isPlayerSleeping())
            {
                entityplayer.wakeUpPlayer(false, false, true);
            }
        }

        world.provider.resetRainAndThunder();
	}

}
