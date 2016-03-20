package stellarium;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import stellarium.client.ClientSettings;
import stellarium.client.StellarClientHook;
import stellarium.client.StellarKeyHook;
import stellarium.config.EnumViewMode;
import stellarium.config.IConfigHandler;
import stellarium.stellars.Optics;
import stellarium.stellars.StellarManager;

public class ClientProxy extends CommonProxy implements IProxy {
	
	private static final String clientConfigCategory = "clientconfig";
	private static final String clientConfigOpticsCategory = "clientconfig.optics";
	
	private ClientSettings settings = new ClientSettings();
	
	public ClientSettings getClientSettings() {
		return this.settings;
	}
	
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
				
		        mag_Limit=config.get(category, "Mag_Limit", 4.0);
		        mag_Limit.comment="Limit of magnitude can be seen on naked eye.\n" +
		        		"If you want to increase FPS, lower the Mag_Limit.\n" +
		        		"(Realistic = 6.5, Default = 4.0)\n" +
		        		"The lower you set it, the fewer stars you will see\n" +
		        		"but the better FPS you will get";
		        mag_Limit.setRequiresMcRestart(true);
		        mag_Limit.setLanguageKey("config.property.client.maglimit");

		        turb=config.get(category, "Twinkling(Turbulance)", 1.0);
		        turb.comment="Degree of the twinkling effect of star.\n"
		        		+ "It determines the turbulance of atmosphere, which actually cause the twinkling effect. "
        				+ "The greater the value, the more the stars will twinkle. Default is 1.0. To disable set to 0.0";
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
		        
		        viewMode.setValue(settings.getViewMode().getName());
		        
		        
		        List<String> propNameList = new ArrayList(Arrays.asList(mag_Limit.getName(),
		        		moon_Frac.getName(), turb.getName(), viewMode.getName(),
		        		minuteLength.getName(), hourToMinute.getName()));
		        config.setCategoryPropertyOrder(category, propNameList);
			}

			@Override
			public void loadFromConfig(Configuration config, String category) {
				settings.mag_Limit=(float)mag_Limit.getDouble();
		        //Scaling
				settings.turb=(float)turb.getDouble() * 4.0f;
		        settings.imgFrac=moon_Frac.getInt();
		        settings.minuteLength = minuteLength.getDouble();
		        settings.anHourToMinute = hourToMinute.getInt();
		        
		        settings.setViewMode(EnumViewMode.getModeForName(viewMode.getString()));
			}
			
		});
		
		cfgManager.register(clientConfigOpticsCategory, Optics.instance);
	}
	
	@Override
	public World getDefWorld() {
		return Minecraft.getMinecraft().theWorld;
	}
}
