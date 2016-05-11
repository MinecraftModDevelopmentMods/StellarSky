package stellarium;

import java.io.IOException;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import stellarapi.api.gui.overlay.OverlayRegistry;
import stellarapi.api.lib.config.ConfigManager;
import stellarium.client.ClientSettings;
import stellarium.client.StellarClientFMLHook;
import stellarium.client.overlay.StellarSkyOverlays;
import stellarium.client.overlay.clientcfg.OverlayClientSettingsType;
import stellarium.client.overlay.clock.OverlayClockType;
import stellarium.stellars.Optics;
import stellarium.stellars.layer.CelestialManager;

public class ClientProxy extends CommonProxy implements IProxy {
	
	public static final String clientConfigCategory = "clientconfig";
	private static final String clientConfigOpticsCategory = "clientconfig.optics";
	
	private ClientSettings clientSettings = new ClientSettings();
	
	private ConfigManager guiConfig;
	private CelestialManager celestialManager = new CelestialManager(true);
	
	public ClientSettings getClientSettings() {
		return this.clientSettings;
	}
	
	@Override
	public CelestialManager getClientCelestialManager() {
		return this.celestialManager;
	}
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
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
		guiConfig.syncFromFile();
		
    	celestialManager.initializeClient(this.clientSettings);
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
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
}
