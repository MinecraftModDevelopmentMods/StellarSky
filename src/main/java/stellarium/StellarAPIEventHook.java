package stellarium;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import stellarapi.api.ICelestialCoordinate;
import stellarapi.api.ISkyEffect;
import stellarapi.api.StellarAPIReference;
import stellarapi.api.celestials.IEffectorType;
import stellarapi.api.event.ConstructCelestialsEvent;
import stellarapi.api.event.ResetCoordinateEvent;
import stellarapi.api.event.ResetSkyEffectEvent;
import stellarapi.api.event.world.ClientWorldEvent;
import stellarapi.api.event.world.ServerWorldEvent;
import stellarapi.api.helper.WorldProviderReplaceHelper;
import stellarium.api.StellarSkyAPI;
import stellarium.stellars.DefaultCelestialHelper;
import stellarium.stellars.StellarManager;
import stellarium.stellars.layer.CelestialManager;
import stellarium.world.StellarDimensionManager;

public class StellarAPIEventHook {
	
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onConstruct(ConstructCelestialsEvent event) {
		StellarDimensionManager dimManager = StellarDimensionManager.get(event.getWorld());
		if(dimManager != null) {
			StellarSky.logger.info("Startng Construction of Celestial Images...");
			StellarAPIReference.resetCoordinate(event.getWorld());
			StellarAPIReference.resetSkyEffect(event.getWorld());
			
			ICelestialCoordinate coordinate = StellarAPIReference.getCoordinate(event.getWorld());
			ISkyEffect sky = StellarAPIReference.getSkyEffect(event.getWorld());
			
			event.getCollections().addAll(dimManager.constructCelestials(coordinate, sky));
			event.getEffectors(IEffectorType.Light).addAll(dimManager.getSuns());
			event.getEffectors(IEffectorType.Tide).addAll(dimManager.getMoons());
			StellarSky.logger.info("Finished Construction of Celestial Images.");
		} else if(!StellarManager.hasSetup(event.getWorld())) {
			StellarSky.logger.info("Delayed Celestial Setup since StellarManager had not been initialized.");
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onReset(ResetCoordinateEvent event) {
		StellarDimensionManager dimManager = StellarDimensionManager.get(event.getWorld());
		if(dimManager != null)
			event.setCoordinate(dimManager.getCoordinate());
		 else if(!StellarManager.hasSetup(event.getWorld())) {
			 StellarSky.logger.info("Delayed Dimension setup since StellarManager had not been initialized.");
			 event.setCanceled(true);
		 }
	}
	
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onReset(ResetSkyEffectEvent event) {
		StellarDimensionManager dimManager = StellarDimensionManager.get(event.getWorld());
		if(dimManager != null)
			event.setSkyEffect(dimManager.getSkySet());
		 else if(!StellarManager.hasSetup(event.getWorld())) {
			 StellarSky.logger.info("Delayed Sky Setup since StellarManager had not been initialized.");
			 event.setCanceled(true);
		 }
	}
	
	
	@SubscribeEvent
	public void onClientWorldLoad(ClientWorldEvent.Load event) {
		IProgressUpdate update = event.getProgressUpdate("StellarSky");
		update.resetProgressAndMessage(I18n.format("progress.text.injection.main"));
		update.displayLoadingString(I18n.format("progress.text.injection.manager"));
		StellarManager manager = StellarManager.loadOrCreateClientManager(event.getWorld());
		
		if(!manager.getSettings().serverEnabled)
			manager.setup(StellarSky.proxy.getClientCelestialManager().copy());
		
		String dimName = event.getWorld().provider.getDimensionType().getName();
		if(!StellarSky.proxy.getServerSettings().serverEnabled)
			handleDimOnServerDisabled(event.getWorld(), manager,update);
		
		if(mark) {
			handleNotHaveModOnServer(event.getWorld(), manager, update);
			mark = false;
		} else if(StellarSky.proxy.getServerSettings().serverEnabled) {
			update.displayLoadingString(I18n.format("progress.text.injection.query", dimName));
			update.setLoadingProgress(0);
			StellarSky.instance.getNetworkManager().queryInformation(event.getWorld());
		}
	}

	@SubscribeEvent(receiveCanceled = true)
	public void onClientWorldLoaded(ClientWorldEvent.Loaded event) {
		if(StellarManager.getClientManager().hasSetup())
			event.getProgressUpdate("StellarSky").displayLoadingString("");
		else {
			event.getProgressUpdate("StellarSky").setLoadingProgress(99);
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public void onServerWorldInitiate(ServerWorldEvent.Initial event) {
		StellarManager manager = StellarManager.loadOrCreateServerManager(event.getServer());
		manager.setup(new CelestialManager(false));
	}
	
	@SubscribeEvent
	public void onServerWorldLoad(ServerWorldEvent.Load event) {
		StellarManager manager = StellarManager.getServerManager(event.getServer());
		String dimName = event.getWorld().provider.getDimensionType().getName();
		if(StellarSky.proxy.getDimensionSettings().hasSubConfig(dimName)) {
			StellarDimensionManager dimManager = StellarDimensionManager.loadOrCreate(event.getWorld(), manager, dimName);
			setupDimension(event.getWorld(), manager, dimManager);
		}
	}
	
	
	public static void setupDimension(World world, StellarManager manager, StellarDimensionManager dimManager) {
		dimManager.setup();
		
		StellarAPIReference.constructCelestials(world);
		StellarAPIReference.resetSkyEffect(world);
		
		ICelestialCoordinate coordinate = StellarAPIReference.getCoordinate(world);
		ISkyEffect skyEffect = StellarAPIReference.getSkyEffect(world);
		
		if(manager.getSettings().serverEnabled && dimManager.getSettings().doesPatchProvider()) {
			DefaultCelestialHelper helper = new DefaultCelestialHelper((float)dimManager.getSettings().getSunlightMultiplier(), 1.0f,
					dimManager.getSuns().get(0), dimManager.getMoons().get(0), coordinate, skyEffect);
			WorldProvider newProvider = StellarSkyAPI.getReplacedWorldProvider(world, world.provider, helper);
			WorldProviderReplaceHelper.patchWorldProviderWith(world, newProvider);
		}
		
		if(world.isRemote)
			StellarSky.proxy.setupSkyRenderer(world.provider, manager.getCelestialManager(), dimManager.getSettings().getSkyRendererType(), dimManager.getLandscapeCache());
	}
	
	
	private static boolean mark = false;
	
	public static void markNotHave() {
		mark = true;
	}
	
	private static void handleNotHaveModOnServer(World world, StellarManager manager, IProgressUpdate update) {
		manager.handleServerWithoutMod();
		
		if(manager.getCelestialManager() == null) {
			manager.setup(StellarSky.proxy.getClientCelestialManager().copy());
			handleDimOnServerDisabled(world, manager, update);
		}
	}
	
	private static void handleDimOnServerDisabled(World world, StellarManager manager, IProgressUpdate update) {
		String dimName = world.provider.getDimensionType().getName();
		if(StellarSky.proxy.getDimensionSettings().hasSubConfig(dimName)) {
			update.displayLoadingString(I18n.format("progress.text.injection.dimmanager", dimName));
			StellarDimensionManager dimManager = StellarDimensionManager.loadOrCreate(world, manager, dimName);
			setupDimension(world, manager, dimManager);
			update.displayLoadingString("");
		}
	}
}
