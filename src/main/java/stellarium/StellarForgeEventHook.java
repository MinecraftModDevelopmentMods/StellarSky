package stellarium;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.google.common.base.Throwables;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.event.world.WorldEvent;
import stellarapi.api.ICelestialCoordinate;
import stellarapi.api.ISkyEffect;
import stellarapi.api.StellarAPIReference;
import stellarapi.api.helper.WorldProviderReplaceHelper;
import stellarium.api.StellarSkyAPI;
import stellarium.render.SkyRenderCelestial;
import stellarium.stellars.DefaultCelestialHelper;
import stellarium.stellars.StellarManager;
import stellarium.stellars.layer.CelestialManager;
import stellarium.world.StellarDimensionManager;

public class StellarForgeEventHook {
	
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
		if(!StellarManager.hasManager(e.world, e.world.isRemote)) {
			manager = StellarManager.loadOrCreateManager(e.world);
		} else manager = StellarManager.getManager(e.world.isRemote);
		
		if(manager.getCelestialManager() == null && (!e.world.isRemote || !manager.getSettings().serverEnabled))
			setupManager(e.world, manager);
		
		String dimName = e.world.provider.getDimensionName();
		if(!e.world.isRemote || !manager.getSettings().serverEnabled)
			if(StellarSky.proxy.dimensionSettings.hasSubConfig(dimName)) {
				StellarDimensionManager dimManager = StellarDimensionManager.loadOrCreate(e.world, manager, dimName);
				setupDimension(e.world, manager, dimManager);
			}
		
		if(e.world.isRemote && mark) {
			handleNotHaveModOnServer(e.world);
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
		
		StellarAPIReference.resetSkyEffect(world);
		StellarAPIReference.constructCelestials(world);
		
		ICelestialCoordinate coordinate = StellarAPIReference.getCoordinate(world);
		ISkyEffect skyEffect = StellarAPIReference.getSkyEffect(world);
		
		if(manager.getSettings().serverEnabled && dimManager.getSettings().doesPatchProvider()) {
			DefaultCelestialHelper helper = new DefaultCelestialHelper(0, 0, null, null, coordinate, skyEffect);
			WorldProvider newProvider = StellarSkyAPI.getReplacedWorldProvider(world, world.provider, helper);
			new WorldProviderReplaceHelper().patchWorldProviderWith(world, newProvider);
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
			StellarForgeEventHook.setupManager(world, manager);
			
			String dimName = world.provider.getDimensionName();
			if(StellarSky.proxy.dimensionSettings.hasSubConfig(dimName)) {
				StellarDimensionManager dimManager = StellarDimensionManager.loadOrCreate(world, manager, dimName);
				StellarForgeEventHook.setupDimension(world, manager, dimManager);
			}
		}
	}
	
	public static void markNotHave() {
		mark = true;
	}
	
}
