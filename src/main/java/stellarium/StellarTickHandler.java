package stellarium;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.Iterator;

import stellarium.stellars.StellarManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.WorldInfo;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;

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
			World world = StellarSky.proxy.getDefWorld();
			
			if(world != null && (world.provider.dimensionId == 0 || world.provider.dimensionId == 1)) {
				StellarManager manager = StellarManager.getManager(world);
				manager.update(world.getWorldTime(),
							world.provider.isSurfaceWorld());
			}
		}
	}
		
	@SubscribeEvent
	public void tickStart(TickEvent.WorldTickEvent e) {
		if(e.phase == Phase.START){
			if(e.world != null) {
				StellarManager manager = StellarManager.getManager(e.world);
				if(!manager.getSettings().serverEnabled)
					return;
				
				if(StellarSky.proxy.wakeManager.isEnabled()) {
					WorldServer world = (WorldServer) e.world;

					world.updateAllPlayersSleepingFlag();
					if (world.areAllPlayersAsleep())
						this.tryWakePlayers(world, manager);

					try {
						sleep.setBoolean(world, false);
					} catch (IllegalArgumentException ex) {
						ex.printStackTrace();
					} catch (IllegalAccessException ex) {
						ex.printStackTrace();
					}
				}
				
				manager.update(e.world.getWorldTime(),
						e.world.provider.isSurfaceWorld());
			}
		}
	}

	private void tryWakePlayers(WorldServer world, StellarManager manager) {		
        if (world.getGameRules().getGameRuleBooleanValue("doDaylightCycle"))
        {
        	WorldInfo info = world.getWorldInfo();
        	long worldTime = info.getWorldTime();
            info.setWorldTime(StellarSky.proxy.wakeManager.getWakeTime(world, manager, worldTime));
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
