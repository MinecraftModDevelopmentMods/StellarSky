package stellarium;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import stellarium.stellars.StellarManager;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;

public class ClientProxy extends CommonProxy implements IProxy {
	
	private static final String clientConfigCategory = "clientconfig";
	
	@Override
	public void preInit(FMLPreInitializationEvent event) throws IOException {
		manager = new StellarManager(Side.CLIENT);
		
    	//Initialize Objects
        config = new Configuration(event.getSuggestedConfigurationFile());
        
        config.load();
        setupConfig();
        config.save();
        
		manager.InitializeStars();
	
		MinecraftForge.EVENT_BUS.register(new StellarClientHook());
		FMLCommonHandler.instance().bus().register(new StellarKeyHook());
	}

	@Override
	public void load(FMLInitializationEvent event) {
		super.load(event);
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
	}
	
	@Override
	public void setupConfig() {
        super.setupConfig();
        
        config.setCategoryComment(clientConfigCategory, "Configurations for client modifications.\n"
        		+ "Most of them are for rendering/view.");
		
        Property Mag_Limit=config.get(clientConfigCategory, "Mag_Limit", 5.0);
        Mag_Limit.comment="Limit of magnitude can be seen on naked eye.\n" +
        		"If you want to increase FPS, you can set this property a bit little (e.g. 0.3)\n" +
        		"and FPS will be exponentially improved";
        manager.Mag_Limit=(float)Mag_Limit.getDouble();

        Property turb=config.get(clientConfigCategory, "Twinkling(Turbulance)", 0.3);
        turb.comment="Degree of the twinkling effect of star.\n"
        		+ "It determines the turbulance of atmosphere, which actually cause the twinkling effect";
        manager.Turb=(float)turb.getDouble();
        
        Property Moon_Frac=config.get(clientConfigCategory, "Moon_Fragments_Number", 16);
        Moon_Frac.comment="Moon is drawn with fragments\n" +
        		"Less fragments will increase FPS, but the moon become more defective";
        manager.ImgFrac=Moon_Frac.getInt();
        
        Property minuteLength = config.get(clientConfigCategory, "Minute_Length", 20.0);
        minuteLength.comment = "Length of minute in tick.";
        manager.minuteLength = minuteLength.getDouble();
        
        Property hourToMinute = config.get(clientConfigCategory, "Hour_Length", 60);
        hourToMinute.comment = "Length of hour in minute.";
        manager.anHourToMinute = hourToMinute.getInt();
   	}
	
	@Override
	public World getDefWorld() {
		return Minecraft.getMinecraft().theWorld;
	}
}
