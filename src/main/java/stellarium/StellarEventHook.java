package stellarium;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.google.common.base.Throwables;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
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
			e.world.provider.setSkyRenderer(new DrawSky());
		}
	}
	
}
