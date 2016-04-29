package stellarium;

import java.io.IOException;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import stellarapi.api.lib.config.ConfigManager;
import stellarium.api.StellarSkyAPI;
import stellarium.client.ClientSettings;
import stellarium.client.DefaultHourProvider;
import stellarium.client.StellarClientFMLHook;
import stellarium.client.StellarClientForgeHook;
import stellarium.client.gui.OverlayHandler;
import stellarium.stellars.Optics;
import stellarium.stellars.layer.CelestialManager;

public class ClientProxy extends CommonProxy implements IProxy {
	
	private static final String clientConfigCategory = "clientconfig";
	private static final String clientConfigOpticsCategory = "clientconfig.optics";
	
	private ClientSettings clientSettings = new ClientSettings();
	
	private ConfigManager guiConfig;
	private OverlayHandler overlay;
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
		
		MinecraftForge.EVENT_BUS.register(new StellarClientForgeHook());
		
		MinecraftForge.EVENT_BUS.register(this.overlay = new OverlayHandler(this.guiConfig));
		
		FMLCommonHandler.instance().bus().register(new StellarClientFMLHook(this.overlay));
		
		StellarSkyAPI.registerHourProvider(new DefaultHourProvider(this.clientSettings));
	}

	@Override
	public void load(FMLInitializationEvent event) throws IOException {
		super.load(event);
		guiConfig.syncFromFile();

		overlay.initialize(Minecraft.getMinecraft());
		
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
	
	@Override
	public World getDefWorld(boolean isRemote) {
		return isRemote? this.getDefWorld() : super.getDefWorld();
	}
	
	public Entity getDefViewerEntity() {
		return Minecraft.getMinecraft().renderViewEntity;
	}
}
