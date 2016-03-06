package stellarium.common;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import stellarium.config.IConfigHandler;

public class CommonSettings implements IConfigHandler {
	
	public boolean serverEnabled;
	public double day, year;
	public int yearOffset, dayOffset;
	public double tickOffset;
	public double latitudeOverworld, latitudeEnder;
	public double longitudeOverworld, longitudeEnder;
	public double moonSizeMultiplier, moonBrightnessMultiplier;

	private Property propServerEnabled;
	private Property propDay, propYear;
	private Property propYearOffset, propDayOffset, propTickOffset;
	private Property propLatitudeOverworld, propLongitudeOverworld;
	private Property propLatitudeEnder, propLongitudeEnder;
	private Property propMoonSize, propMoonBrightness;
	
	public CommonSettings() { }
	
	public CommonSettings(CommonSettings settingsToCopy) {
		this.serverEnabled = settingsToCopy.serverEnabled;
		this.day = settingsToCopy.day;
		this.year = settingsToCopy.year;
		this.yearOffset = settingsToCopy.yearOffset;
		this.dayOffset = settingsToCopy.dayOffset;
		this.tickOffset = settingsToCopy.tickOffset;
		this.latitudeOverworld = settingsToCopy.latitudeOverworld;
		this.longitudeOverworld = settingsToCopy.longitudeOverworld;
		this.latitudeEnder = settingsToCopy.latitudeEnder;
		this.longitudeEnder = settingsToCopy.longitudeEnder;
		this.moonSizeMultiplier = settingsToCopy.moonSizeMultiplier;
		this.moonBrightnessMultiplier = settingsToCopy.moonBrightnessMultiplier;
	}

	@Override
	public void setupConfig(Configuration config, String category) {
		config.setCategoryComment(category, "Configurations for server modifications.");
		config.setCategoryLanguageKey(category, "config.category.server");
		config.setCategoryRequiresWorldRestart(category, true);
		
		List<String> propNameList = Lists.newArrayList();
		
        propServerEnabled=config.get(category, "Server_Enabled", true);
        propServerEnabled.comment="Enables Server-Side Sky change.";
        propServerEnabled.setRequiresWorldRestart(true);
        propServerEnabled.setLanguageKey("config.property.server.serverenabled");
        propNameList.add(propServerEnabled.getName());
        
        propDay=config.get(category, "Day_Length", 24000.0);
        propDay.comment="Length of a day, in a tick.";
        propDay.setRequiresWorldRestart(true);
        propDay.setLanguageKey("config.property.server.daylength");
        propNameList.add(propDay.getName());
        
        propYear=config.get(category, "Year_Length", 365.25);
        propYear.comment="Length of a year, in a day.";
        propYear.setRequiresWorldRestart(true);
        propYear.setLanguageKey("config.property.server.yearlength");
        propNameList.add(propYear.getName());

       	propYearOffset = config.get(category, "Year_Offset", 0);
       	propYearOffset.comment = "Year offset on world starting time.";
       	propYearOffset.setRequiresWorldRestart(true);
       	propYearOffset.setLanguageKey("config.property.server.yearoffset");
        propNameList.add(propYearOffset.getName());

       	propDayOffset = config.get(category, "Day_Offset", 0);
       	propDayOffset.comment = "Day offset on world starting time.";
       	propDayOffset.setRequiresWorldRestart(true);
       	propDayOffset.setLanguageKey("config.property.server.dayoffset");
        propNameList.add(propDayOffset.getName());

       	propTickOffset = config.get(category, "Tick_Offset", 5000.0);
       	propTickOffset.comment = "Tick offset on world starting time.";
       	propTickOffset.setRequiresWorldRestart(true);
       	propTickOffset.setLanguageKey("config.property.server.tickoffset");
        propNameList.add(propTickOffset.getName());

       	propLatitudeOverworld = config.get(category, "Latitude_Overworld", 37.5);
       	propLatitudeOverworld.comment = "Latitude on Overworld, in Degrees.";
       	propLatitudeOverworld.setRequiresWorldRestart(true);
       	propLatitudeOverworld.setLanguageKey("config.property.server.latitudeoverworld");
        propNameList.add(propLatitudeOverworld.getName());

       	propLongitudeOverworld = config.get(category, "Longitude_Overworld", 0.0);
       	propLongitudeOverworld.comment = "Longitude on Overworld, in Degrees. (East is +)";
       	propLongitudeOverworld.setRequiresWorldRestart(true);
       	propLongitudeOverworld.setLanguageKey("config.property.server.longitudeoverworld");
        propNameList.add(propLongitudeOverworld.getName());

       	propLatitudeEnder = config.get(category, "Latitude_Ender", -52.5);
       	propLatitudeEnder.comment = "Latitude on Ender, in Degrees.";
       	propLatitudeEnder.setRequiresWorldRestart(true);
       	propLatitudeEnder.setLanguageKey("config.property.server.latitudeender");
        propNameList.add(propLatitudeEnder.getName());

       	propLongitudeEnder = config.get(category, "Longitude_Ender", 180.0);
       	propLongitudeEnder.comment = "Longitude on Ender, in Degrees. (East is +)";
       	propLongitudeEnder.setRequiresWorldRestart(true);
       	propLongitudeEnder.setLanguageKey("config.property.server.longitudeender");
        propNameList.add(propLongitudeEnder.getName());
        
       	propMoonSize = config.get(category, "Moon_Size", 1.0);
       	propMoonSize.comment = "Size of moon. (Default size is 1.0)";
       	propMoonSize.setRequiresWorldRestart(true);
       	propMoonSize.setLanguageKey("config.property.server.moonsize");
        propNameList.add(propMoonSize.getName());
        
       	propMoonBrightness = config.get(category, "Moon_Brightness", 1.0);
       	propMoonBrightness.comment = "Brightness of moon. (Default brightness is 1.0)";
       	propMoonBrightness.setRequiresWorldRestart(true);
       	propMoonBrightness.setLanguageKey("config.property.server.moonbrightness");
        propNameList.add(propMoonBrightness.getName());
        config.setCategoryPropertyOrder(category, propNameList);
	}

	@Override
	public void loadFromConfig(Configuration config, String category) {
        this.serverEnabled = propServerEnabled.getBoolean();
        this.day = propDay.getDouble();
        this.year = propYear.getDouble();
       	this.yearOffset = propYearOffset.getInt();
       	this.dayOffset = propDayOffset.getInt();
       	this.tickOffset = propTickOffset.getDouble();
       	this.latitudeOverworld = propLatitudeOverworld.getDouble();
       	this.longitudeOverworld = propLongitudeOverworld.getDouble();
       	this.latitudeEnder = propLatitudeEnder.getDouble();
       	this.longitudeEnder = propLongitudeEnder.getDouble();
       	this.moonSizeMultiplier = propMoonSize.getDouble();
       	this.moonBrightnessMultiplier = propMoonBrightness.getDouble();
	}

	
	public void readFromNBT(NBTTagCompound compound) {
		// TODO Auto-generated method stub
		
	}

	
	public void writeToNBT(NBTTagCompound compound) {
		// TODO Auto-generated method stub
		
	}
}
