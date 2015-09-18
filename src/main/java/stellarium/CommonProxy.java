package stellarium;

import java.io.IOException;

import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import stellarium.stellars.StellarManager;

public class CommonProxy implements IProxy {

	public StellarManager manager;
	public Configuration config;
	
	@Override
	public void preInit(FMLPreInitializationEvent event) throws IOException {
		manager = new StellarManager(Side.SERVER);
		
        config = new Configuration(event.getSuggestedConfigurationFile());
        
        config.load();
        setupConfig();
        config.save();
	}

	@Override
	public void load(FMLInitializationEvent event) {
		manager.Initialize();
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		
	}

	
	public void setupConfig() {
        Property serverEnabled=config.get(Configuration.CATEGORY_GENERAL, "Server_Enabled", true);
        serverEnabled.comment="Enables Server-Side Sky change.\n";
        manager.serverEnabled=serverEnabled.getBoolean(true);
        
        Property day=config.get(Configuration.CATEGORY_GENERAL, "Day_Length", 24000.0);
        day.comment="Length of a day, in a tick.\n";
        manager.day=day.getDouble(24000.0);
        
        Property year=config.get(Configuration.CATEGORY_GENERAL, "Year_Length", 365.25);
        year.comment="Length of a year, in a day.\n";
        manager.year=year.getDouble(365.25);
	}

	@Override
	public World getDefWorld() {
		return null;
	}
}
