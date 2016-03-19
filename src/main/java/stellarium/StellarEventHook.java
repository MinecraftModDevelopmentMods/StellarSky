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
		
		if(StellarSky.getManager().serverEnabled && e.world.provider.getDimension() == 0) {
			try {
				providerField.set(e.world, new StellarWorldProvider(e.world, e.world.provider));
			} catch (Exception exc) {
				Throwables.propagate(exc);
			}
		}

		if(!e.world.isRemote)
			return;
				
		if(e.world.provider.getDimension() == 0 || e.world.provider.getDimension() == -1)
		{
			e.world.provider.setSkyRenderer(new DrawSky());
		}
	}
	
	@SubscribeEvent
	public void onSleepInBed(PlayerSleepInBedEvent event) {
		if(!StellarSky.proxy.wakeManager.isEnabled()) {
			return;
		}

		if(event.result == null || event.result == EnumStatus.OK || event.result == EnumStatus.NOT_POSSIBLE_NOW) {
			World worldObj = event.entityPlayer.worldObj;
			if (!StellarSky.proxy.wakeManager.canSkipTime(worldObj, worldObj.getWorldTime()))
				event.result = EntityPlayer.EnumStatus.NOT_POSSIBLE_NOW;
		}
	}
}
