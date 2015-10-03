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
			
			if(world != null)
				StellarSky.getManager().Update(world.getWorldTime(),
						world.provider.isSurfaceWorld());
		}
	}
		
	@SubscribeEvent
	public void tickStart(TickEvent.WorldTickEvent e) {
		if(e.phase == Phase.START){
			if(e.world != null) {
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
				StellarSky.getManager().Update(e.world.getWorldTime(),
						e.world.provider.isSurfaceWorld());
			}
		}
	}

	private void tryWakePlayers(WorldServer world) {
		double dayLength = StellarSky.getManager().day;
		double tickOffset = StellarSky.getManager().tickOffset;
		
		if(!StellarSky.getManager().serverEnabled)
			dayLength = 24000.0;
		
        if (world.getGameRules().getGameRuleBooleanValue("doDaylightCycle"))
        {
        	WorldInfo info = world.getWorldInfo();
        	long worldTime = info.getWorldTime();
        	double modifiedWorldTime = worldTime - worldTime % dayLength - tickOffset;
        	while(modifiedWorldTime < worldTime)
        		modifiedWorldTime += dayLength;
        	
            info.setWorldTime((long) modifiedWorldTime);
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
