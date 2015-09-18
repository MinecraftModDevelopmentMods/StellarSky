package stellarium;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import stellarium.stellars.StellarManager;

public class ClientProxy extends CommonProxy implements IProxy {
	
	@Override
	public void preInit(FMLPreInitializationEvent event) throws IOException {
		manager = new StellarManager(Side.CLIENT);
		
    	//Initialize Objects
        config = new Configuration(event.getSuggestedConfigurationFile());
        
        config.load();
        setupConfig();
        config.save();
        
		manager.InitializeStars();
	
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
		
        Property Mag_Limit=config.get(Configuration.CATEGORY_GENERAL, "Mag_Limit", 5.0);
        Mag_Limit.comment="Limit of magnitude can be seen on naked eye.\n" +
        		"If you want to increase FPS, you can set this property a bit little (e.g. 0.3)\n" +
        		"and FPS will be exponentially improved";
        manager.Mag_Limit=(float)Mag_Limit.getDouble(5.0);

        Property turb=config.get(Configuration.CATEGORY_GENERAL, "Twinkling(Turbulance)", 0.3);
        turb.comment="Degree of the twinkling effect of star.\n"
        		+ "It determines the turbulance of atmosphere, which actually cause the twinkling effect";
        manager.Turb=(float)turb.getDouble(0.3);
        
        Property Moon_Frac=config.get(Configuration.CATEGORY_GENERAL, "Moon_Fragments_Number", 16);
        Moon_Frac.comment="Moon is drawn with fragments\n" +
        		"Less fragments will increase FPS, but the moon become more defective\n";
        manager.ImgFrac=Moon_Frac.getInt(16);
   	}
	
	@Override
	public World getDefWorld() {
		return Minecraft.getMinecraft().theWorld;
	}
}
