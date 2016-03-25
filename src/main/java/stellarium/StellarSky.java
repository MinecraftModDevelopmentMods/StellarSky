package stellarium;

import java.io.IOException;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import stellarium.api.StellarSkyAPI;
import stellarium.command.CommandLock;
import stellarium.common.SkyProviderGetter;
import stellarium.compat.CompatManager;
import stellarium.sync.StellarNetworkManager;

@Mod(modid=StellarSky.modid, version=StellarSky.version,
	dependencies="required-after:sciapi@[1.3.0.0,1.4.0.0);after:CalendarAPI@[1.1,2.0)", guiFactory="stellarium.config.StellarConfigGuiFactory")
public class StellarSky {

	public static final String modid = "stellarsky";
	public static final String version = "@VERSION@";

        // The instance of Stellarium
        @Instance(StellarSky.modid)
        public static StellarSky instance;
        
        @SidedProxy(clientSide="stellarium.ClientProxy", serverSide="stellarium.CommonProxy")
        public static CommonProxy proxy;
        
        private StellarEventHook eventHook = new StellarEventHook();
        private StellarTickHandler tickHandler = new StellarTickHandler();
        private StellarFMLEventHook fmlEventHook = new StellarFMLEventHook();
        private StellarNetworkManager networkManager = new StellarNetworkManager();
        
        public StellarNetworkManager getNetworkManager() {
        	return this.networkManager;
        }
        
        @EventHandler
        public void preInit(FMLPreInitializationEvent event) {        	
        	proxy.preInit(event);

    		MinecraftForge.EVENT_BUS.register(eventHook);
    		MinecraftForge.EVENT_BUS.register(tickHandler);
    		MinecraftForge.EVENT_BUS.register(fmlEventHook);
    		MinecraftForge.EVENT_BUS.register(this.networkManager);
    		
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