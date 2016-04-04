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
import stellarium.api.StellarSkyAPI;
import stellarium.command.CommandLock;
import stellarium.common.SkyProviderGetter;
import stellarium.compat.CompatManager;
import stellarium.sync.StellarNetworkEventHandler;
import stellarium.sync.StellarNetworkFMLEventHandler;
import stellarium.sync.StellarNetworkManager;

@Mod(modid=StellarSky.modid, version=StellarSky.version,
	dependencies="required-after:sciapi@[1.1.0.0,1.2.0.0)", guiFactory="stellarium.config.StellarConfigGuiFactory")
public class StellarSky {
	
		public static final String modid = "stellarsky";
		public static final String version = "0.1.24";

        // The instance of Stellarium
        @Instance(StellarSky.modid)
        public static StellarSky instance;
        
        @SidedProxy(clientSide="stellarium.ClientProxy", serverSide="stellarium.CommonProxy")
        public static CommonProxy proxy;
        
        public static Logger logger;
        
        private StellarEventHook eventHook = new StellarEventHook();
        private StellarTickHandler tickHandler = new StellarTickHandler();
        private StellarFMLEventHook fmlEventHook = new StellarFMLEventHook();
        private StellarNetworkManager networkManager = new StellarNetworkManager();
        
        public StellarNetworkManager getNetworkManager() {
        	return this.networkManager;
        }
        
        @EventHandler
        public void preInit(FMLPreInitializationEvent event) { 
        	logger = event.getModLog();
        	
        	proxy.preInit(event);
        	
    		MinecraftForge.EVENT_BUS.register(this.eventHook);
    		FMLCommonHandler.instance().bus().register(this.tickHandler);
    		FMLCommonHandler.instance().bus().register(this.fmlEventHook);
    		
    		MinecraftForge.EVENT_BUS.register(new StellarNetworkEventHandler(this.networkManager));
    		FMLCommonHandler.instance().bus().register(new StellarNetworkFMLEventHandler(this.networkManager));
    		
    		StellarSkyAPI.setSkyProviderGetter(new SkyProviderGetter());
    		CompatManager.getInstance().onPreInit();
        }
        
        @EventHandler
        public void load(FMLInitializationEvent event) throws IOException {
        	proxy.load(event);
        	
    		CompatManager.getInstance().onInit();
        }
        
        @EventHandler
        public void postInit(FMLPostInitializationEvent event) {
        	proxy.postInit(event);
        	
    		CompatManager.getInstance().onPostInit();
        }
        
        @EventHandler
        public void serverStarting(FMLServerStartingEvent event) {
        	event.registerServerCommand(new CommandLock());
        }
}