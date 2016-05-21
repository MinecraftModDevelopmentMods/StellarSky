package stellarium.display.ecgrid;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import stellarium.display.PerDisplaySettings;

public class EcGridSettings extends PerDisplaySettings {

	public boolean displayEnabled, eclipticEnabled, gridEnabled;
	public int displayFrag;
	public double displayAlpha;
	public double[] displayBaseColor;
	public double[] displayHeightColor;
	public double[] displayAzimuthColor;
	
	private Property propDisplayEnabled, propDisplayAlpha, propDisplayFrag;
	private Property propEclipticEnabled, propGridEnabled;
	private Property propDisplayBaseColor, propDisplayLatitudeColor, propDisplayLongitudeColor;
	
	@Override
	public void setupConfig(Configuration config, String category) {
		config.setCategoryComment(category, "Configurations for Display of ecliptictal Coordinate Grid.");
		config.setCategoryLanguageKey(category, "config.category.display.eccoord");
		config.setCategoryRequiresMcRestart(category, false);
		
		super.setupConfig(config, category);
		
		List<String> propNameList = Lists.newArrayList();
		
        propDisplayEnabled=config.get(category, "Display_Enabled", false);
        propDisplayEnabled.setComment("Set to true to enable display of ecliptictal coordinates.");
        propDisplayEnabled.setRequiresMcRestart(false);
        propDisplayEnabled.setLanguageKey("config.property.display.enabled");
        propNameList.add(propDisplayEnabled.getName());
        
        propDisplayAlpha=config.get(category, "Display_Alpha", 0.05);
        propDisplayAlpha.setComment("Alpha(Brightness) of the display.");
        propDisplayAlpha.setRequiresMcRestart(false);
        propDisplayAlpha.setMinValue(0.0).setMaxValue(0.2);
        propDisplayAlpha.setLanguageKey("config.property.display.alpha");
        propNameList.add(propDisplayAlpha.getName());
        
        propDisplayFrag=config.get(category, "Display_Fragments_Number", 16);
        propDisplayFrag.setComment("Number of fragments of display grids in direction of latitude.");
        propDisplayFrag.setRequiresMcRestart(false);
        propDisplayFrag.setMinValue(4).setMaxValue(64);
        propDisplayFrag.setLanguageKey("config.property.display.eccoord.fragments");
        propNameList.add(propDisplayFrag.getName());
        
        propEclipticEnabled=config.get(category, "Display_Ecliptic_Enabled", true);
        propEclipticEnabled.setComment("Set to true to enable display of ecliptic.");
        propEclipticEnabled.setRequiresMcRestart(false);
        propEclipticEnabled.setLanguageKey("config.property.display.eccoord.ecliptic.displayed");
        propNameList.add(propEclipticEnabled.getName());
        
        propGridEnabled=config.get(category, "Display_Grid_Enabled", true);
        propGridEnabled.setComment("Set to true to enable display of ecliptictal grid.");
        propGridEnabled.setRequiresMcRestart(false);
        propGridEnabled.setLanguageKey("config.property.display.eccoord.grid.displayed");
        propNameList.add(propGridEnabled.getName());
        
        propDisplayBaseColor=config.get(category, "Display_Base_Color", new double[] {0.5, 0.5, 0.0});
        propDisplayBaseColor.setComment("Base color factor, the grid tends to have this color as base.");
        propDisplayBaseColor.setIsListLengthFixed(true);
        propDisplayBaseColor.setRequiresMcRestart(false);
        propDisplayBaseColor.setLanguageKey("config.property.display.eccoord.color.base");
        propNameList.add(propDisplayBaseColor.getName());
        
        propDisplayLatitudeColor=config.get(category, "Display_Latitude_Color", new double[] {1.0, 0.0, 0.0});
        propDisplayLatitudeColor.setComment("Color factor for latitude, the grid tends to have this color when latitude gets bigger.");
        propDisplayLatitudeColor.setIsListLengthFixed(true);
        propDisplayLatitudeColor.setRequiresMcRestart(false);
        propDisplayLatitudeColor.setLanguageKey("config.property.display.eccoord.color.latitude");
        propNameList.add(propDisplayLatitudeColor.getName());

        propDisplayLongitudeColor=config.get(category, "Display_Longitude_Color", new double[] {0.0, 1.0, 0.0});
        propDisplayLongitudeColor.setComment("Color factor for longitude, the grid tends to have this color when longitude gets bigger.");
        propDisplayLongitudeColor.setIsListLengthFixed(true);
        propDisplayLongitudeColor.setRequiresMcRestart(false);
        propDisplayLongitudeColor.setLanguageKey("config.property.display.eccoord.color.longitude");
        propNameList.add(propDisplayLongitudeColor.getName());
        
        config.setCategoryPropertyOrder(category, propNameList);
	}

	@Override
	public void loadFromConfig(Configuration config, String category) {
		this.displayEnabled = propDisplayEnabled.getBoolean();
		this.displayAlpha = propDisplayAlpha.getDouble();
		this.displayFrag = propDisplayFrag.getInt();
		this.displayBaseColor = propDisplayBaseColor.getDoubleList();
		this.displayHeightColor = propDisplayLatitudeColor.getDoubleList();
		this.displayAzimuthColor = propDisplayLongitudeColor.getDoubleList();
		this.gridEnabled = propGridEnabled.getBoolean();
		this.eclipticEnabled = propEclipticEnabled.getBoolean();
	}

	@Override
	public void saveToConfig(Configuration config, String category) { }

}
