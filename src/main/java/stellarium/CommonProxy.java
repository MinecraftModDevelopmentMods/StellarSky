package stellarium;

import java.io.IOException;

import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import stellarium.stellars.StellarManager;
import stellarium.world.StellarWorldProvider;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;

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
		if(manager.serverEnabled)
		{
			DimensionManager.unregisterDimension(0);
			DimensionManager.unregisterProviderType(0);
			DimensionManager.registerProviderType(0, StellarWorldProvider.class, true);
			DimensionManager.registerDimension(0, 0);
		}
	}

	
	public void setupConfig() {
        Property serverEnabled=config.get(Configuration.CATEGORY_GENERAL, "Server_Enabled", true);
        serverEnabled.comment="Enables Server-Side Sky change.\n";
        manager.serverEnabled=serverEnabled.getBoolean(true);
        
        Property day=config.get(Configuration.CATEGORY_GENERAL, "Day_Length", 24000.0);
        day.comment="Length of a day, in a tick.\n";
        manager.day=day.getDouble(24000.0);
        
        Property year=config.get(Configuration.CATEGORY_GENERAL, "Year_Length", 365.25);
        year.comment="Length of an year, in a day.\n";
        manager.year=year.getDouble(365.25);
	}
}
