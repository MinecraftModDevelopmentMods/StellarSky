package stellarium;

import java.io.IOException;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.client.IRenderHandler;
import stellarapi.api.ICelestialCoordinate;
import stellarapi.api.ISkyEffect;
import stellarapi.api.StellarAPIReference;
import stellarapi.api.gui.overlay.OverlayRegistry;
import stellarapi.api.lib.config.ConfigManager;
import stellarium.api.StellarSkyAPI;
import stellarium.client.ClientSettings;
import stellarium.client.StellarClientFMLHook;
import stellarium.client.overlay.StellarSkyOverlays;
import stellarium.client.overlay.clientcfg.OverlayClientSettingsType;
import stellarium.client.overlay.clock.OverlayClockType;
import stellarium.display.DisplayManager;
import stellarium.display.DisplayRegistry;
import stellarium.render.SkyCelestialRenderer;
import stellarium.render.shader.ShaderHelper;
import stellarium.stellars.Optics;
import stellarium.stellars.layer.CelestialManager;
import stellarium.stellars.layer.StellarLayerRegistry;
import stellarium.world.landscape.LandscapeCache;
import stellarium.world.landscape.LandscapeClientSettings;

public class ClientProxy extends CommonProxy implements IProxy {
	
	public static final String clientConfigCategory = "clientconfig";
	private static final String clientConfigOpticsCategory = "clientconfig.optics";
	
	private ClientSettings clientSettings = new ClientSettings();
	private LandscapeClientSettings landscapeSettings = new LandscapeClientSettings();
	
	private ConfigManager guiConfig;
	private CelestialManager celestialManager = new CelestialManager(true);
	private DisplayManager displayManager = new DisplayManager();
		
	public ClientSettings getClientSettings() {
		return this.clientSettings;
	}
	
	@Override
	public CelestialManager getClientCelestialManager() {
		return this.celestialManager;
	}
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		this.guiConfig = new ConfigManager(
				StellarSkyReferences.getConfiguration(event.getModConfigurationDirectory(),
						StellarSkyReferences.guiSettings));
						
		FMLCommonHandler.instance().bus().register(new StellarClientFMLHook());
		
		OverlayRegistry.registerOverlaySet("stellarsky", new StellarSkyOverlays());
		OverlayRegistry.registerOverlay("clock", new OverlayClockType(), this.guiConfig);
		OverlayRegistry.registerOverlay("clientconfig", new OverlayClientSettingsType(), this.guiConfig);
	}

	@Override
	public void load(FMLInitializationEvent event) throws IOException {
		super.load(event);
		
		StellarLayerRegistry.getInstance().composeSettings(this.clientSettings);
		DisplayRegistry.getInstance().setupDisplay(this.clientSettings, this.displayManager);
		clientSettings.putSubConfig("landscape", this.landscapeSettings);
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
		
		guiConfig.syncFromFile();
    	celestialManager.initializeClient(this.clientSettings);
	}
	
	@Override
	public void setupCelestialConfigManager(ConfigManager manager) {
		super.setupCelestialConfigManager(manager);
		manager.register(clientConfigCategory, this.clientSettings);
		manager.register(clientConfigOpticsCategory, Optics.instance);
	}
	
	@Override
	public World getDefWorld() {
		return Minecraft.getMinecraft().theWorld;
	}
	
	public Entity getDefViewerEntity() {
		return Minecraft.getMinecraft().renderViewEntity;
	}
	
	@Override
	public int getRenderDistanceSettings() {
		return Minecraft.getMinecraft().gameSettings.renderDistanceChunks;
	}
	
	@Override
	public void setupSkyRenderer(WorldProvider provider, CelestialManager celManager, String skyType, LandscapeCache cache) {
		IRenderHandler renderer = StellarSkyAPI.getRendererFor(skyType,
				new SkyCelestialRenderer(this.clientSettings, celManager, this.displayManager, this.landscapeSettings, cache));
		provider.setSkyRenderer(renderer);
	}
	
	@Override
	public float getScreenWidth() {
		return Minecraft.getMinecraft().displayWidth;
	}
	
	@Override
	public void updateTick() {		
		ICelestialCoordinate coordinate = StellarAPIReference.getCoordinate(StellarSky.proxy.getDefWorld());
		ISkyEffect sky = StellarAPIReference.getSkyEffect(StellarSky.proxy.getDefWorld());
		displayManager.updateDisplay(this.clientSettings, coordinate, sky);
	}
}
