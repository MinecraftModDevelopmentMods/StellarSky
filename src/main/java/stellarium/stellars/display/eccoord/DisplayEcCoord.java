package stellarium.stellars.display.eccoord;

import java.util.List;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import stellarium.render.ICelestialObjectRenderer;
import stellarium.stellars.display.DisplayElement;
import stellarium.stellars.display.IDisplayRenderCache;

public class DisplayEcCoord extends DisplayElement {

	public boolean displayEnabled;
	public int displayFrag;
	public double displayAlpha;
	public double[] displayBaseColor;
	public double[] displayHeightColor;
	public double[] displayAzimuthColor;
	
	private Property propDisplayEnabled, propDisplayAlpha, propDisplayFrag;
	private Property propDisplayBaseColor, propDisplayLatitudeColor, propDisplayLongitudeColor;
	
	@Override
	public void setupConfig(Configuration config, String category) {
		config.setCategoryComment(category, "Configurations for Display of Horizontal Coordinate Grid.");
		config.setCategoryLanguageKey(category, "config.category.display.eccoord");
		config.setCategoryRequiresMcRestart(category, false);
		
		List<String> propNameList = Lists.newArrayList();
		
        propDisplayEnabled=config.get(category, "Display_Enabled", false);
        propDisplayEnabled.comment="Set to true to enable display of horizontal coordinates.";
        propDisplayEnabled.setRequiresMcRestart(false);
        propDisplayEnabled.setLanguageKey("config.property.display.enabled");
        propNameList.add(propDisplayEnabled.getName());
        
        propDisplayAlpha=config.get(category, "Display_Alpha", 0.1);
        propDisplayAlpha.comment="Alpha(Brightness) of the display.";
        propDisplayAlpha.setRequiresMcRestart(false);
        propDisplayAlpha.setLanguageKey("config.property.display.alpha");
        propNameList.add(propDisplayAlpha.getName());
        
        propDisplayFrag=config.get(category, "Display_Fragments_Number", 16);
        propDisplayFrag.comment="Number of fragments of display grids in direction of latitude.";
        propDisplayFrag.setRequiresMcRestart(false);
        propDisplayFrag.setLanguageKey("config.property.display.eccoord.fragments");
        propNameList.add(propDisplayFrag.getName());
        
        propDisplayBaseColor=config.get(category, "Display_Base_Color", new double[] {0.5, 0.5, 0.0});
        propDisplayBaseColor.comment = "Base color factor, the grid tends to have this color as base.";
        propDisplayBaseColor.setIsListLengthFixed(true);
        propDisplayBaseColor.setRequiresMcRestart(false);
        propDisplayBaseColor.setLanguageKey("config.property.display.eccoord.color.base");
        propNameList.add(propDisplayBaseColor.getName());
        
        propDisplayLatitudeColor=config.get(category, "Display_Latitude_Color", new double[] {1.0, 0.0, 0.0});
        propDisplayLatitudeColor.comment = "Color factor for latitude, the grid tends to have this color when latitude gets bigger.";
        propDisplayLatitudeColor.setIsListLengthFixed(true);
        propDisplayLatitudeColor.setRequiresMcRestart(false);
        propDisplayLatitudeColor.setLanguageKey("config.property.eccoord.display.color.latitude");
        propNameList.add(propDisplayLatitudeColor.getName());

        propDisplayLongitudeColor=config.get(category, "Display_Longitude_Color", new double[] {0.0, 1.0, 0.0});
        propDisplayLongitudeColor.comment = "Color factor for longitude, the grid tends to have this color when longitude gets bigger.";
        propDisplayLongitudeColor.setIsListLengthFixed(true);
        propDisplayLongitudeColor.setRequiresMcRestart(false);
        propDisplayLongitudeColor.setLanguageKey("config.property.eccoord.display.color.longitude");
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
	}

	@Override
	public void saveToConfig(Configuration config, String category) {
		// TODO save to configuration; not done till now		
	}
	
	@Override
	public String getID() {
		return "Ecliptic_Coordinate_Grid";
	}

	@Override
	public IDisplayRenderCache generateCache() {
		return new DisplayEcCoordCache();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ICelestialObjectRenderer getRenderer() {
		return new DisplayEcCoordRenderer();
	}

}
