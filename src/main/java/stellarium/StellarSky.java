package stellarium;

import java.io.IOException;

import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import stellarapi.api.StellarAPIReference;
import stellarapi.api.lib.config.ConfigManager;
import stellarium.api.DefaultSkyType;
import stellarium.api.SkyRenderTypeSurface;
import stellarium.api.StellarSkyAPI;
import stellarium.command.CommandLock;
import stellarium.render.SkyRenderTypeEnd;
import stellarium.render.SkyTypeEnd;
import stellarium.sync.StellarNetworkManager;
import stellarium.world.provider.DefaultWorldProviderReplacer;
import stellarium.world.provider.EndReplacer;

@Mod(modid=StellarSkyReferences.modid, version=StellarSkyReferences.version,
	dependencies="required-after:StellarAPI@[0.4.4.1, 0.4.5.0)", guiFactory="stellarium.client.config.StellarConfigGuiFactory")
public class StellarSky {
	
		// The instance of Stellarium
        @Instance(StellarSkyReferences.modid)
        public static StellarSky instance;
        
        @SidedProxy(clientSide="stellarium.ClientProxy", serverSide="stellarium.CommonProxy")
        public static IProxy proxy;
        
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
    		MinecraftForge.EVENT_BUS.register(this.tickHandler);
    		MinecraftForge.EVENT_BUS.register(this.fmlEventHook);

    		StellarAPIReference.getEventBus().register(new StellarAPIEventHook());
    		
    		StellarSkyAPI.setDefaultReplacer(new DefaultWorldProviderReplacer());
    		StellarSkyAPI.registerWorldProviderReplacer(new EndReplacer());
    		
    		StellarSkyAPI.registerSkyType("Overworld", new DefaultSkyType());
    		StellarSkyAPI.registerSkyType("The End", new SkyTypeEnd());
    		
    		StellarSkyAPI.registerRendererType(new SkyRenderTypeSurface());
    		StellarSkyAPI.registerRendererType(new SkyRenderTypeEnd());
    		
    		StellarSkyResources.init();
        }
        
        @EventHandler
        public void load(FMLInitializationEvent event) throws IOException {
        	proxy.load(event);
        }
        
        @EventHandler
        public void postInit(FMLPostInitializationEvent event) {
        	celestialConfigManager.syncFromFile();
        	proxy.postInit(event);
        }
        
        @EventHandler
        public void serverStarting(FMLServerStartingEvent event) {
        	event.registerServerCommand(new CommandLock());
        }
}