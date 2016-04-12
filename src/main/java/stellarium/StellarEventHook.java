package stellarium;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.google.common.base.Throwables;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.EnumStatus;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import stellarium.api.IStellarWorldProvider;
import stellarium.api.StellarSkyAPI;
import stellarium.render.SkyRenderCelestial;
import stellarium.stellars.StellarManager;
import stellarium.stellars.StellarSkyProvider;
import stellarium.stellars.layer.CelestialManager;
import stellarium.stellars.view.StellarDimensionManager;

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
		StellarManager manager;
		if(!StellarManager.hasManager(e.getWorld(), e.getWorld().isRemote)) {
			manager = StellarManager.loadOrCreateManager(e.getWorld());
		} else manager = StellarManager.getManager(e.getWorld().isRemote);
		
		if(manager.getCelestialManager() == null && (!e.getWorld().isRemote || !manager.getSettings().serverEnabled))
			setupManager(e.getWorld(), manager);
		
		String dimName = e.getWorld().provider.getDimensionType().getName();
		if(!e.getWorld().isRemote || !manager.getSettings().serverEnabled)
			if(StellarSky.proxy.dimensionSettings.hasSubConfig(dimName)) {
				StellarDimensionManager dimManager = StellarDimensionManager.loadOrCreate(e.getWorld(), manager, dimName);
				setupDimension(e.getWorld(), manager, dimManager);
			}
		
		if(e.getWorld().isRemote && mark) {
			handleNotHaveModOnServer(e.getWorld());
			mark = false;
		}
	}
	
	public static void setupManager(World world, StellarManager manager) {
		if(world.isRemote)
			manager.setup(StellarSky.proxy.getClientCelestialManager());
		else manager.setup(new CelestialManager(false));
	}
	
	public static void setupDimension(World world, StellarManager manager, StellarDimensionManager dimManager) {
		dimManager.setup();
		
		if(manager.getSettings().serverEnabled && dimManager.getSettings().doesPatchProvider()) {
			try {
				WorldProvider newProvider = StellarSkyAPI.getReplacedWorldProvider(world, world.provider);
				if(newProvider instanceof IStellarWorldProvider)
					((IStellarWorldProvider) newProvider).setSkyProvider(new StellarSkyProvider(world, world.provider, manager, dimManager));
				providerField.set(world, newProvider);
			} catch (Exception exc) {
				Throwables.propagate(exc);
			}
		}
		
		if(world.isRemote)
		{
			IRenderHandler renderer = StellarSkyAPI.getRendererFor(dimManager.getSettings().getSkyRendererType(), new SkyRenderCelestial());
			world.provider.setSkyRenderer(renderer);
		}
	}
	
	private static boolean mark = false;
	
	private static void handleNotHaveModOnServer(World world) {
		StellarManager manager = StellarManager.loadOrCreateManager(world);
		manager.handleServerWithoutMod();
		
		if(manager.getCelestialManager() == null)
		{
			StellarEventHook.setupManager(world, manager);
			
			String dimName = world.provider.getDimensionType().getName();
			if(StellarSky.proxy.dimensionSettings.hasSubConfig(dimName)) {
				StellarDimensionManager dimManager = StellarDimensionManager.loadOrCreate(world, manager, dimName);
				StellarEventHook.setupDimension(world, manager, dimManager);
			}
		}
	}
	
	public static void markNotHave() {
		mark = true;
	}
	
	@SubscribeEvent
	public void onSleepInBed(PlayerSleepInBedEvent event) {
		if(!StellarSky.proxy.wakeManager.isEnabled() || event.getEntityPlayer().worldObj.isRemote) {
			return;
		}

		if(event.getResultStatus() == null || event.getResultStatus() == EnumStatus.OK || event.getResultStatus() == EnumStatus.NOT_POSSIBLE_NOW) {
			World worldObj = event.getEntityPlayer().worldObj;
			if (!StellarSky.proxy.wakeManager.canSkipTime(worldObj, StellarSkyAPI.getSkyProvider(worldObj), worldObj.getWorldTime()))
				event.setResult(EntityPlayer.EnumStatus.NOT_POSSIBLE_NOW);
		}
	}
	
}
