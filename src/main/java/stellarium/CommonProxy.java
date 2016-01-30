package stellarium;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import stellarium.config.ConfigManager;
import stellarium.config.IConfigHandler;
import stellarium.sleepwake.AlarmWakeHandler;
import stellarium.sleepwake.LightWakeHandler;
import stellarium.sleepwake.SleepWakeManager;
import stellarium.stellars.StellarManager;

public class CommonProxy implements IProxy {

	protected StellarManager manager;
	protected Configuration config;
	protected ConfigManager cfgManager;
	public SleepWakeManager wakeManager = new SleepWakeManager();
	
	private static final String serverConfigCategory = "serverconfig";
	private static final String serverConfigWakeCategory = "serverconfig.wake";
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		manager = new StellarManager(Side.SERVER);
		
		this.setupConfigManager(event.getSuggestedConfigurationFile());
	}

	@Override
	public void load(FMLInitializationEvent event) throws IOException {
        cfgManager.syncFromFile();
		manager.initialize();
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		
	}
	
	public void setupConfigManager(File file) {
		config = new Configuration(file);
        cfgManager = new ConfigManager(config);
        
        cfgManager.register(serverConfigCategory, new IConfigHandler() {
        	Property serverEnabled;
        	Property day;
        	Property year;
        	Property yearOffset;
        	Property dayOffset;
        	Property tickOffset;
        	Property latitudeOverworld;
        	Property longitudeOverworld;
        	Property latitudeEnder;
        	Property longitudeEnder;
        	Property moonSize;
        	Property moonBrightness;
        	
			@Override
			public void setupConfig(Configuration config, String category) {
				config.setCategoryComment(category, "Configurations for server modifications.");
				config.setCategoryLanguageKey(category, "config.category.server");
				config.setCategoryRequiresMcRestart(category, true);
				
				List<String> propNameList = Lists.newArrayList();
				
		        serverEnabled=config.get(category, "Server_Enabled", true);
		        serverEnabled.comment="Enables Server-Side Sky change.";
		        serverEnabled.setRequiresMcRestart(true);
		        serverEnabled.setLanguageKey("config.property.server.serverenabled");
		        propNameList.add(serverEnabled.getName());
		        
		        day=config.get(category, "Day_Length", 24000.0);
		        day.comment="Length of a day, in a tick.";
		        day.setRequiresMcRestart(true);
		        day.setLanguageKey("config.property.server.daylength");
		        propNameList.add(day.getName());
		        
		        year=config.get(category, "Year_Length", 365.25);
		        year.comment="Length of a year, in a day.";
		        year.setRequiresMcRestart(true);
		        year.setLanguageKey("config.property.server.yearlength");
		        propNameList.add(year.getName());

		       	yearOffset = config.get(category, "Year_Offset", 0);
		       	yearOffset.comment = "Year offset on world starting time.";
		       	yearOffset.setRequiresMcRestart(true);
		       	yearOffset.setLanguageKey("config.property.server.yearoffset");
		        propNameList.add(yearOffset.getName());

		       	dayOffset = config.get(category, "Day_Offset", 0);
		       	dayOffset.comment = "Day offset on world starting time.";
		       	dayOffset.setRequiresMcRestart(true);
		       	dayOffset.setLanguageKey("config.property.server.dayoffset");
		        propNameList.add(dayOffset.getName());

		       	tickOffset = config.get(category, "Tick_Offset", 5000.0);
		       	tickOffset.comment = "Tick offset on world starting time.";
		       	tickOffset.setRequiresMcRestart(true);
		       	tickOffset.setLanguageKey("config.property.server.tickoffset");
		        propNameList.add(tickOffset.getName());

		       	latitudeOverworld = config.get(category, "Latitude_Overworld", 37.5);
		       	latitudeOverworld.comment = "Latitude on Overworld, in Degrees.";
		       	latitudeOverworld.setRequiresMcRestart(true);
		       	latitudeOverworld.setLanguageKey("config.property.server.latitudeoverworld");
		        propNameList.add(latitudeOverworld.getName());

		       	longitudeOverworld = config.get(category, "Longitude_Overworld", 0.0);
		       	longitudeOverworld.comment = "Longitude on Overworld, in Degrees. (East is +)";
		       	longitudeOverworld.setRequiresMcRestart(true);
		       	longitudeOverworld.setLanguageKey("config.property.server.longitudeoverworld");
		        propNameList.add(longitudeOverworld.getName());

		       	latitudeEnder = config.get(category, "Latitude_Ender", -52.5);
		       	latitudeEnder.comment = "Latitude on Ender, in Degrees.";
		       	latitudeEnder.setRequiresMcRestart(true);
		       	latitudeEnder.setLanguageKey("config.property.server.latitudeender");
		        propNameList.add(latitudeEnder.getName());

		       	longitudeEnder = config.get(category, "Longitude_Ender", 180.0);
		       	longitudeEnder.comment = "Longitude on Ender, in Degrees. (East is +)";
		       	longitudeEnder.setRequiresMcRestart(true);
		       	longitudeEnder.setLanguageKey("config.property.server.longitudeender");
		        propNameList.add(longitudeEnder.getName());

		       	moonSize = config.get(category, "Moon_Size", 1.0);
		       	moonSize.comment = "Size of moon. (Default size is 1.0)";
		       	moonSize.setRequiresMcRestart(true);
		       	moonSize.setLanguageKey("config.property.server.moonsize");
		        propNameList.add(moonSize.getName());

		       	moonBrightness = config.get(category, "Moon_Brightness", 1.0);
		       	moonBrightness.comment = "Brightness of moon. (Default brightness is 1.0)";
		       	moonBrightness.setRequiresMcRestart(true);
		       	moonBrightness.setLanguageKey("config.property.server.moonbrightness");
		        propNameList.add(moonBrightness.getName());
		        config.setCategoryPropertyOrder(category, propNameList);
			}

			@Override
			public void loadFromConfig(Configuration config, String category) {
		        manager.serverEnabled=serverEnabled.getBoolean();
		        manager.day=day.getDouble();
		        manager.year=year.getDouble();
		       	manager.yearOffset = yearOffset.getInt();
		       	manager.dayOffset = dayOffset.getInt();
		       	manager.tickOffset = tickOffset.getDouble();
		       	manager.latitudeOverworld = latitudeOverworld.getDouble();
		       	manager.longitudeOverworld = longitudeOverworld.getDouble();
		       	manager.latitudeEnder = latitudeEnder.getDouble();
		       	manager.longitudeEnder = longitudeEnder.getDouble();
		       	manager.moonSizeMultiplier = moonSize.getDouble();
		       	manager.moonBrightnessMultiplier = moonBrightness.getDouble();
			}
        	
        });
        
        cfgManager.register(serverConfigWakeCategory, wakeManager);
        wakeManager.register("wakeByBright", new LightWakeHandler(), true);
        wakeManager.register("wakeByAlarm", new AlarmWakeHandler(), false);
	}
	
	public ConfigManager getCfgManager() {
		return this.cfgManager;
	}
	
	@Override
	public World getDefWorld() {
		return null;
	}

	public Configuration getConfig() {
		return this.config;
	}
}
