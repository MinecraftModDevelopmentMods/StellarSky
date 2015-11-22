package stellarium;

import java.io.IOException;

import net.minecraftforge.common.MinecraftForge;
import net.minecraft.item.Item;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.common.*;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import stellarium.stellars.StellarManager;
import stellarium.world.StellarWorldProvider;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLModDisabledEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.*;
import cpw.mods.fml.relauncher.Side;

@Mod(modid=StellarSky.modid, name=StellarSky.name, version=StellarSky.version,
	dependencies="required-after:sciapi", guiFactory="stellarium.config.StellarConfigGuiFactory")
public class StellarSky {
	
		public static final String modid = "stellarsky";
		public static final String name = "Stellar Sky";
		public static final String version = "0.1.16b";

        // The instance of Stellarium
        @Instance(StellarSky.modid)
        public static StellarSky instance;
        
        @SidedProxy(clientSide="stellarium.ClientProxy", serverSide="stellarium.CommonProxy")
        public static CommonProxy proxy;
        
        public StellarEventHook eventHook = new StellarEventHook();
        public StellarTickHandler tickHandler = new StellarTickHandler();
        public StellarFMLEventHook fmlEventHook = new StellarFMLEventHook();
        
        public static StellarManager getManager() { return proxy.manager; }
        
        @EventHandler
        public void preInit(FMLPreInitializationEvent event) {
        	proxy.preInit(event);
        	
    		MinecraftForge.EVENT_BUS.register(eventHook);
    		FMLCommonHandler.instance().bus().register(tickHandler);
    		FMLCommonHandler.instance().bus().register(fmlEventHook);
        }
        
        @EventHandler
        public void load(FMLInitializationEvent event) throws IOException {
        	proxy.load(event);
        }
        
        @EventHandler
        public void postInit(FMLPostInitializationEvent event) {
        	proxy.postInit(event);
        }
        
}