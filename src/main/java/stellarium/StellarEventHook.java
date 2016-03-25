package stellarium;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.google.common.base.Throwables;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.EnumStatus;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import stellarium.stellars.StellarManager;
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
		if(e.world.provider.getDimensionId() == 0 || e.world.provider.getDimensionId() == 1) {
			StellarManager manager = StellarManager.loadOrCreateManager(e.world);
			manager.setRemote(e.world.isRemote);
			setupManager(e.world, manager);
		}
		
		if(!e.world.isRemote)
			return;
		
		if(e.world.provider.getDimensionId() == 0 || e.world.provider.getDimensionId() == 1)
			e.world.provider.setSkyRenderer(new SkyRenderer());
	}
	
	public static void setupManager(World world, StellarManager manager) {
		manager.initialize();
		
		if(manager.getSettings().serverEnabled) {
			try {
				providerField.set(world, new StellarWorldProvider(world, world.provider, manager));
			} catch (Exception exc) {
				Throwables.propagate(exc);
			}
		}
		
		if(world.isRemote && (world.provider.getDimensionId() == 0 || world.provider.getDimensionId() == 1))
			world.provider.setSkyRenderer(new SkyRenderer());
	}
	
	@SubscribeEvent
	public void onSleepInBed(PlayerSleepInBedEvent event) {
		if(!StellarSky.proxy.wakeManager.isEnabled() || event.entityPlayer.worldObj.isRemote) {
			return;
		}

		if(event.result == null || event.result == EnumStatus.OK || event.result == EnumStatus.NOT_POSSIBLE_NOW) {
			World worldObj = event.entityPlayer.worldObj;
			StellarManager manager = StellarManager.getManager(false);
			if (!StellarSky.proxy.wakeManager.canSkipTime(worldObj, manager, worldObj.getWorldTime()))
				event.result = EntityPlayer.EnumStatus.NOT_POSSIBLE_NOW;
		}
	}
}
