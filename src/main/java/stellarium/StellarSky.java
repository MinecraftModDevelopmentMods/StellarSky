package stellarium;

import java.io.IOException;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import stellarium.stellars.StellarManager;

@Mod(modid=StellarSky.modid, name=StellarSky.name, version=StellarSky.version, dependencies="required-after:sciapi")
public class StellarSky {
	
		public static final String modid = "stellarsky";
		public static final String name = "Stellar Sky";
		public static final String version = "0.1.10";

        // The instance of Stellarium
        @Instance(StellarSky.modid)
        public static StellarSky instance;
        
        @SidedProxy(clientSide="stellarium.ClientProxy", serverSide="stellarium.CommonProxy")
        public static CommonProxy proxy;
        
        public StellarEventHook eventHook = new StellarEventHook();
        public StellarTickHandler tickHandler = new StellarTickHandler();
        
        public static StellarManager getManager() { return proxy.manager; }
        
        @EventHandler
        public void preInit(FMLPreInitializationEvent event) throws IOException{
        	
        	proxy.preInit(event);
        	
    		MinecraftForge.EVENT_BUS.register(eventHook);
    		FMLCommonHandler.instance().bus().register(tickHandler);
    		
        }
        
        @EventHandler
        public void load(FMLInitializationEvent event) {
        	proxy.load(event);
        }
        
        @EventHandler
        public void postInit(FMLPostInitializationEvent event) {
        	proxy.postInit(event);
        }
        
}