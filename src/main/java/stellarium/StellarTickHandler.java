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
import net.minecraftforge.fml.relauncher.Side;

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
			
			if(world != null)
				StellarSky.getManager().update(world.getWorldTime(),
						world.provider.isSurfaceWorld());
		}
	}
		
	@SubscribeEvent
	public void tickStart(TickEvent.WorldTickEvent e) {
		if(e.phase == TickEvent.Phase.START){
			if(e.world != null &&
					StellarSky.getManager().serverEnabled &&
					StellarSky.proxy.wakeManager.isEnabled()) {
				WorldServer world = (WorldServer) e.world;
				
				world.updateAllPlayersSleepingFlag();
		        if (world.areAllPlayersAsleep())
		        	this.tryWakePlayers(world);
				
				try {
					sleep.setBoolean(world, false);
				} catch (IllegalArgumentException ex) {
					ex.printStackTrace();
				} catch (IllegalAccessException ex) {
					ex.printStackTrace();
				}
			}
			
			if(!(StellarSky.getManager().getSide() == Side.SERVER
					&& StellarSky.getManager().serverEnabled))
				return;
			
			if(e.world != null)
			{
				StellarSky.getManager().update(e.world.getWorldTime(),
						e.world.provider.isSurfaceWorld());
			}
		}
	}

	private void tryWakePlayers(WorldServer world) {
		double dayLength = StellarSky.getManager().day;
		double tickOffset = StellarSky.getManager().tickOffset;
		
        if (world.getGameRules().getBoolean("doDaylightCycle"))
        {
        	WorldInfo info = world.getWorldInfo();
        	long worldTime = info.getWorldTime();
            info.setWorldTime(StellarSky.proxy.wakeManager.getWakeTime(world, worldTime));
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
