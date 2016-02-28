package stellarium;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

import com.google.common.base.Throwables;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.EnumStatus;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.world.WorldEvent;
import stellarium.client.SkyRenderer;
import stellarium.world.StellarWorldProvider;

public class StellarEventHook {
	
	private static Field providerField = ReflectionHelper.findField(World.class,
			ObfuscationReflectionHelper.remapFieldNames(World.class.getName(), "provider", "field_73011_w"));
	
	static {
		try {
			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(providerField, providerField.getModifiers() & ~ Modifier.FINAL);
		} catch(Exception exc) {
			Throwables.propagate(exc);
		}
	}
	
	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load e)
	{
		StellarSky.getManager().initializePlanet();

		if(StellarSky.getManager().serverEnabled && e.world.provider.dimensionId == 0) {
			try {
				providerField.set(e.world, new StellarWorldProvider(e.world.provider));
			} catch (Exception exc) {
				Throwables.propagate(exc);
			}
		}

		if(!e.world.isRemote)
			return;
		
		if(e.world.provider.dimensionId == 0 || e.world.provider.dimensionId == -1)
		{
			e.world.provider.setSkyRenderer(new SkyRenderer());
		}
	}
	
	@SubscribeEvent
	public void onSleepInBed(PlayerSleepInBedEvent event) {
		if(!StellarSky.proxy.wakeManager.isEnabled()) {
			return;
		}
		
		if(event.result == null || event.result == EnumStatus.OK || event.result == EnumStatus.NOT_POSSIBLE_NOW)
		{
			World world = event.entityPlayer.worldObj;
			EntityPlayer player = event.entityPlayer;

			if(world.isRemote)
				return;
			
            if (player.isPlayerSleeping() || !player.isEntityAlive())
            {
                event.result = EnumStatus.OTHER_PROBLEM;
            }

            if (!world.provider.isSurfaceWorld())
            {
            	event.result = EnumStatus.NOT_POSSIBLE_HERE;
            }

            if (!StellarSky.proxy.wakeManager.canSkipTime(world, world.getWorldTime()))
            {
                event.result = EnumStatus.NOT_POSSIBLE_NOW;
            }

            if (Math.abs(player.posX - (double)event.x) > 3.0D || Math.abs(player.posY - (double)event.y) > 2.0D || Math.abs(player.posZ - (double)event.z) > 3.0D)
            {
                event.result = EnumStatus.TOO_FAR_AWAY;
            }

            double d0 = 8.0D;
            double d1 = 5.0D;
            List list = world.getEntitiesWithinAABB(EntityMob.class, AxisAlignedBB.getBoundingBox((double)event.x - d0, (double)event.y - d1, (double)event.z - d0, (double)event.x + d0, (double)event.y + d1, (double)event.z + d0));

            if (!list.isEmpty())
            {
                event.result = EnumStatus.NOT_SAFE;
            }
			
            if(event.result == EnumStatus.OK)
            	world.updateAllPlayersSleepingFlag();
		}
	}
	
}
