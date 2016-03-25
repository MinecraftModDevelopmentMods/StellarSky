package stellarium;

import java.lang.reflect.Field;
import java.util.Iterator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import stellarium.stellars.StellarManager;

public class StellarTickHandler {
	
	private Field sleep;
			
	public StellarTickHandler() {
		sleep = getField(WorldServer.class, "allPlayersSleeping", "field_73068_P");
	}
	
	public static Field getField(Class<?> clazz, String... fieldNames) {
		return ReflectionHelper.findField(clazz, ObfuscationReflectionHelper.remapFieldNames(clazz.getName(), fieldNames));
	}
	
	@SubscribeEvent
	public void clientTick(TickEvent.ClientTickEvent e) {
		if(e.phase == TickEvent.Phase.END){
			World world = StellarSky.proxy.getDefWorld();
			
			if(world != null && (world.provider.getDimension() == 0 || world.provider.getDimension() == 1)) {
				StellarManager manager = StellarManager.getManager(true);
				manager.update(world.getWorldTime(),
							world.provider.isSurfaceWorld());
			}
		}
	}
		
	@SubscribeEvent
	public void worldTick(TickEvent.WorldTickEvent e) {
		if(e.phase == TickEvent.Phase.END){
			if(e.world != null) {
				StellarManager manager = StellarManager.getManager(false);
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
				
				if(e.world.provider.getDimension() == 0)
					manager.update(e.world.getWorldTime(),
							e.world.provider.isSurfaceWorld());
			}
		}
	}

	private void tryWakePlayers(WorldServer world, StellarManager manager) {		
        if (world.getGameRules().getBoolean("doDaylightCycle"))
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
