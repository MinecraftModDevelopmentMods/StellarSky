package stellarium;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import stellarium.config.EnumViewMode;
import stellarium.config.IConfigHandler;
import stellarium.stellars.Optics;
import stellarium.stellars.StellarManager;

public class ClientProxy extends CommonProxy implements IProxy {
	
	private static final String clientConfigCategory = "clientconfig";
	private static final String clientConfigOpticsCategory = "clientconfig.optics";
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		manager = new StellarManager(Side.CLIENT);
		
        this.setupConfigManager(event.getSuggestedConfigurationFile());
        
		MinecraftForge.EVENT_BUS.register(new StellarClientHook());
		FMLCommonHandler.instance().bus().register(new StellarKeyHook());
	}

	@Override
	public void load(FMLInitializationEvent event) throws IOException {
		super.load(event);
		
		manager.initializeStars();
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
	}
	
	@Override
	public void setupConfigManager(File file) {
		super.setupConfigManager(file);
		cfgManager.register(clientConfigCategory, new IConfigHandler() {
			
			Property mag_Limit, turb, moon_Frac, minuteLength, hourToMinute, viewMode;
			
			@Override
			public void setupConfig(Configuration config, String category) {
		        config.setCategoryComment(category, "Configurations for client modifications.\n"
		        		+ "Most of them are for rendering/view.");
		        config.setCategoryLanguageKey(category, "config.category.client");
		        config.setCategoryRequiresMcRestart(category, false);
				
		        mag_Limit=config.get(category, "Mag_Limit", 6.5);
		        mag_Limit.comment="Limit of magnitude can be seen on naked eye.\n" +
		        		"If you want to increase FPS, you can set this property a bit lower (e.g. 0.3)\n" +
		        		"and FPS will be exponentially improved";
		        mag_Limit.setRequiresMcRestart(false);
		        mag_Limit.setLanguageKey("config.property.client.maglimit");

		        turb=config.get(category, "Twinkling(Turbulance)", 4.0);
		        turb.comment="Degree of the twinkling effect of star.\n"
		        		+ "It determines the turbulance of atmosphere, which actually cause the twinkling effect. "
        				+ "The greater the value, the more the stars will twinkle. Default is 4.0. To disable set to 0.0";
		        turb.setRequiresMcRestart(false);
		        turb.setLanguageKey("config.property.client.turbulance");
		        
		        moon_Frac=config.get(category, "Moon_Fragments_Number", 16);
		        moon_Frac.comment="Moon is drawn with fragments\n" +
		        		"Less fragments will increase FPS, but the moon will become more defective";
		        moon_Frac.setRequiresMcRestart(false);
		        moon_Frac.setLanguageKey("config.property.client.moonfrac");
		        
		        minuteLength = config.get(category, "Minute_Length", 16.666);
		        minuteLength.comment = "Number of ticks in a minute. (The minute & hour is displayed on HUD as HH:MM format)";
		        minuteLength.setRequiresMcRestart(false);
		        minuteLength.setLanguageKey("config.property.client.minutelength");
		        
		        hourToMinute = config.get(category, "Hour_Length", 60);
		        hourToMinute.comment = "Number of minutes in an hour. (The minute & hour is displayed on HUD as HH:MM format)";
		        hourToMinute.setRequiresMcRestart(false);
		        hourToMinute.setLanguageKey("config.property.client.hourlength");
		        
		        viewMode = config.get(category, "Mode_HUD_Time_View", "empty")
		        		.setValidValues(EnumViewMode.names);
		        viewMode.comment = "Mode for HUD time view.\n"
		        		+ " 3 modes available: empty, hhmm, tick.\n"
		        		+ "Can also be changed in-game using key.";
		        viewMode.setRequiresMcRestart(false);
		        viewMode.setLanguageKey("config.property.client.modeview");
		        
		        viewMode.setValue(manager.getViewMode().getName());
		        
		        
		        List<String> propNameList = Arrays.asList(mag_Limit.getName(),
		        		moon_Frac.getName(), turb.getName(), viewMode.getName(),
		        		minuteLength.getName(), hourToMinute.getName());
		        config.setCategoryPropertyOrder(category, propNameList);
			}

			@Override
			public void loadFromConfig(Configuration config, String category) {
		        manager.mag_Limit=(float)mag_Limit.getDouble();
		        manager.turb=(float)turb.getDouble();
		        manager.imgFrac=moon_Frac.getInt();
		        manager.minuteLength = minuteLength.getDouble();
		        manager.anHourToMinute = hourToMinute.getInt();
		        
		        manager.setViewMode(EnumViewMode.getModeForName(viewMode.getString()));
			}
			
		});
		
		cfgManager.register(clientConfigOpticsCategory, Optics.instance);
	}
	
	@Override
	public World getDefWorld() {
		return Minecraft.getMinecraft().theWorld;
	}
}
