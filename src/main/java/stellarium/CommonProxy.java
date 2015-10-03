package stellarium;

import java.io.IOException;

import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
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
	
	private static final String serverConfigCategory = "serverconfig";
	
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
		config.setCategoryComment(serverConfigCategory, "Configurations for server modifications.");
		
        Property serverEnabled=config.get(serverConfigCategory, "Server_Enabled", true);
        serverEnabled.comment="Enables Server-Side Sky change.";
        manager.serverEnabled=serverEnabled.getBoolean();
        
        Property day=config.get(serverConfigCategory, "Day_Length", 24000.0);
        day.comment="Length of a day, in a tick.";
        manager.day=day.getDouble();
        
        Property year=config.get(serverConfigCategory, "Year_Length", 365.25);
        year.comment="Length of a year, in a day.";
        manager.year=year.getDouble();
        
       	Property yearOffset = config.get(serverConfigCategory, "Year_Offset", 0);
       	yearOffset.comment = "Year offset on world starting time.";
       	manager.yearOffset = yearOffset.getInt();
       	
       	Property dayOffset = config.get(serverConfigCategory, "Day_Offset", 0);
       	dayOffset.comment = "Day offset on world starting time.";
       	manager.dayOffset = dayOffset.getInt();
       	
       	Property tickOffset = config.get(serverConfigCategory, "Tick_Offset", 5000.0);
       	tickOffset.comment = "Tick offset on world starting time.";
       	manager.tickOffset = tickOffset.getDouble();
       	
       	Property lattitudeOverworld = config.get(serverConfigCategory, "Lattitude_Overworld", 37.5);
       	lattitudeOverworld.comment = "Lattitude on Overworld, in Degrees.";
       	manager.lattitudeOverworld = lattitudeOverworld.getDouble();
       	
       	Property longitudeOverworld = config.get(serverConfigCategory, "Longitude_Overworld", 0.0);
       	longitudeOverworld.comment = "Longitude on Overworld, in Degrees. (East is +)";
       	manager.longitudeOverworld = longitudeOverworld.getDouble();
       	
       	Property lattitudeEnder = config.get(serverConfigCategory, "Lattitude_Ender", -52.5);
       	lattitudeEnder.comment = "Lattitude on Ender, in Degrees.";
       	manager.lattitudeEnder = lattitudeEnder.getDouble();
       	
       	Property longitudeEnder = config.get(serverConfigCategory, "Longitude_Ender", 180.0);
       	longitudeEnder.comment = "Longitude on Ender, in Degrees. (East is +)";
       	manager.longitudeEnder = longitudeEnder.getDouble();
  	}

	@Override
	public World getDefWorld() {
		return null;
	}
}
