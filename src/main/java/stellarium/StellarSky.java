package stellarium;

import java.io.IOException;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;
import stellarapi.api.SAPIReferences;
import stellarapi.api.lib.config.ConfigManager;
import stellarium.api.SkyRenderTypeSurface;
import stellarium.api.SkySetTypeDefault;
import stellarium.api.StellarSkyAPI;
import stellarium.command.CommandLock;
import stellarium.render.SkyRenderTypeEnd;
import stellarium.render.SkySetTypeEnd;
import stellarium.sync.StellarNetworkManager;
import stellarium.world.StellarPack;

@Mod(modid=StellarSkyReferences.MODID, version=StellarSkyReferences.VERSION,
acceptedMinecraftVersions="[1.12.0, 1.13.0)",
dependencies="required-after:stellarapi@[1.12.2-0.4.2.3, 1.12.2-0.4.3.0)", guiFactory="stellarium.client.config.StellarConfigGuiFactory")
public class StellarSky {

	// The instance of Stellar Sky
	@Mod.Instance(StellarSkyReferences.MODID)
	public static StellarSky INSTANCE;

	@SidedProxy(clientSide="stellarium.ClientProxy", serverSide="stellarium.CommonProxy")
	public static IProxy PROXY;


	private Logger logger;
	private ConfigManager celestialConfigManager;
	private StellarForgeEventHook eventHook = new StellarForgeEventHook();
	private StellarTickHandler tickHandler = new StellarTickHandler();
	private StellarNetworkManager networkManager;

	public Logger getLogger() {
		return this.logger;
	}

	public StellarNetworkManager getNetworkManager() {
		return this.networkManager;
	}

	public ConfigManager getCelestialConfigManager() {
		return this.celestialConfigManager;
	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) { 
		this.logger = event.getModLog();

		this.celestialConfigManager = new ConfigManager(
				StellarSkyReferences.getConfiguration(event.getModConfigurationDirectory(),
						StellarSkyReferences.CELESTIAL_SETTINGS));


		PROXY.setupCelestialConfigManager(this.celestialConfigManager);
		PROXY.preInit(event);

		this.networkManager = new StellarNetworkManager();

		MinecraftForge.EVENT_BUS.register(this.eventHook);
		MinecraftForge.EVENT_BUS.register(this.tickHandler);

		StellarSkyResources.init();

		SAPIReferences.registerPack(StellarPack.INSTANCE);
	}

	@Mod.EventHandler
	public void load(FMLInitializationEvent event) throws IOException {
		PROXY.load(event);

		StellarSkyAPI.registerSkyType(SAPIReferences.exactOverworld(), new SkySetTypeDefault());
		StellarSkyAPI.registerSkyType(SAPIReferences.overworldType(), new SkySetTypeDefault());
		StellarSkyAPI.registerSkyType(SAPIReferences.endType(), new SkySetTypeEnd());

		StellarSkyAPI.registerRendererType(new SkyRenderTypeSurface());
		StellarSkyAPI.registerRendererType(new SkyRenderTypeEnd());
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		celestialConfigManager.syncFromFile();
		PROXY.postInit(event);
	}

	@Mod.EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandLock());
	}


	public boolean existOnServer() {
		return this.existOnServer;
	}

	private boolean existOnServer = true;

	@NetworkCheckHandler
	public boolean checkNetwork(Map<String, String> modsNversions, Side from) {
		if(from.isServer())
			this.existOnServer = modsNversions.containsKey(StellarSkyReferences.MODID);
		return true;
		// Does not work well, it's just too late
	}
}