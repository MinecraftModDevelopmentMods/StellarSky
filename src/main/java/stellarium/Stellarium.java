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
import stellarium.initials.CConstructManager;
import stellarium.stellars.StellarManager;
import stellarium.stellars.orbit.*;
import stellarium.stellars.cbody.*;
import stellarium.world.StellarWorldProvider;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.*;
import cpw.mods.fml.relauncher.Side;

@Mod(modid=Stellarium.modid, name=Stellarium.name, version=Stellarium.version, dependencies="required-after:sciapi")
public class Stellarium {
	
		public static final String modid = "stellarium";
		public static final String name = "Stellarium";
		public static final String version = "0.1.5";

        // The instance of Stellarium
        @Instance(Stellarium.modid)
        public static Stellarium instance;
        
//        public static ITickHandler tickhandler=new StellarTickHandler();
        public StellarManager manager;
        
        @SidedProxy(clientSide="stellarium.ClientProxy", serverSide="stellarium.ServerProxy")
        public static BaseProxy proxy;
        
        @EventHandler
        public void preInit(FMLPreInitializationEvent event) throws IOException{
        	//Initialize Objects
            Configuration config = new Configuration(event.getSuggestedConfigurationFile());
            
            config.load();
            Property Mag_Limit=config.get(Configuration.CATEGORY_GENERAL, "Mag_Limit", 5.0);
            Mag_Limit.comment="Limit of magnitude can be seen on naked eye.\n" +
            		"If you want to increase FPS, you can set this property a bit little (e.g. 0.3)\n" +
            		"and FPS will be exponentially improved";
            StellarManager.Mag_Limit=(float)Mag_Limit.getDouble(5.0);

            config.load();
            Property turb=config.get(Configuration.CATEGORY_GENERAL, "Twinkling(Turbulance)", 0.3);
            turb.comment="Degree of the twinkling effect of star.\n"
            		+ "It determines the turbulance of atmosphere, which actually cause the twinkling effect";
            StellarManager.Turb=(float)turb.getDouble(0.3);
            
            Property Moon_Frac=config.get(Configuration.CATEGORY_GENERAL, "Moon_Fragments_Number", 16);
            Moon_Frac.comment="Moon is drawn with fragments\n" +
            		"Less fragments will increase FPS, but the moon become more defective\n";
            StellarManager.ImgFrac=Moon_Frac.getInt(16);
            
            config.save();
            
            manager = new StellarManager();
			StellarManager.InitializeStars();
			proxy.InitSided(manager);
			
			MinecraftForge.EVENT_BUS.register(new StellarEventHook());
        }
        
        @EventHandler
        public void load(FMLInitializationEvent event) {

			StellarManager.Initialize();
        	
        	
        }
        
        @EventHandler
        public void postInit(FMLPostInitializationEvent event) {
        	
        	DimensionManager.unregisterDimension(0);
        	DimensionManager.unregisterProviderType(0);
        	DimensionManager.registerProviderType(0, StellarWorldProvider.class, true);
        	DimensionManager.registerDimension(0, 0);
        }
}