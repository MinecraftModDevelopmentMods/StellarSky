package stellarium;

import java.io.IOException;

import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import stellarapi.api.StellarAPIReference;
import stellarapi.api.lib.config.ConfigManager;
import stellarium.api.StellarSkyAPI;
import stellarium.command.CommandLock;
import stellarium.render.SkyRenderTypeEnd;
import stellarium.render.SkyRenderTypeOverworld;
import stellarium.sync.StellarNetworkManager;
import stellarium.world.provider.DefaultWorldProviderReplacer;
import stellarium.world.provider.EndReplacer;

@Mod(modid=StellarSkyReferences.modid, version=StellarSkyReferences.version,
	dependencies="required-after:StellarAPI@[0.1.2.0, 0.1.3.0)", guiFactory="stellarium.config.StellarConfigGuiFactory")
public class StellarSky {
	
		// The instance of Stellarium
        @Instance(StellarSkyReferences.modid)
        public static StellarSky instance;
        
        @SidedProxy(clientSide="stellarium.ClientProxy", serverSide="stellarium.CommonProxy")
        public static CommonProxy proxy;
        
        public static Logger logger;
        
        private ConfigManager celestialConfigManager;
        private StellarForgeEventHook eventHook = new StellarForgeEventHook();
        private StellarTickHandler tickHandler = new StellarTickHandler();
        private StellarFMLEventHook fmlEventHook = new StellarFMLEventHook();
        private StellarNetworkManager networkManager;
        
        public StellarNetworkManager getNetworkManager() {
        	return this.networkManager;
        }
        
		public ConfigManager getCelestialConfigManager() {
			return this.celestialConfigManager;
		}
        
        @EventHandler
        public void preInit(FMLPreInitializationEvent event) { 
        	logger = event.getModLog();
        	
        	this.celestialConfigManager = new ConfigManager(
        			StellarSkyReferences.getConfiguration(event.getModConfigurationDirectory(),
        					StellarSkyReferences.celestialSettings));
        	
            proxy.setupCelestialConfigManager(this.celestialConfigManager);
        	proxy.preInit(event);
        	
        	this.networkManager = new StellarNetworkManager();
        	
    		MinecraftForge.EVENT_BUS.register(this.eventHook);
    		FMLCommonHandler.instance().bus().register(this.tickHandler);
    		FMLCommonHandler.instance().bus().register(this.fmlEventHook);
    		
    		StellarAPIReference.getEventBus().register(new StellarAPIEventHook());
    		
    		StellarSkyAPI.setDefaultReplacer(new DefaultWorldProviderReplacer());
    		StellarSkyAPI.registerWorldProviderReplacer(new EndReplacer());
    		
    		StellarSkyAPI.registerRendererType(new SkyRenderTypeOverworld());
    		StellarSkyAPI.registerRendererType(new SkyRenderTypeEnd());
    		
    		StellarSkyResources.init();
        }
        
        @EventHandler
        public void load(FMLInitializationEvent event) throws IOException {
        	celestialConfigManager.syncFromFile();
        	proxy.load(event);
        }
        
        @EventHandler
        public void postInit(FMLPostInitializationEvent event) {
        	proxy.postInit(event);
        }
        
        @EventHandler
        public void serverStarting(FMLServerStartingEvent event) {
        	event.registerServerCommand(new CommandLock());
        }
}