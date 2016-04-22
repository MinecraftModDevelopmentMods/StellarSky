package stellarium.stellars.display.horcoord;

import java.util.List;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import stellarium.render.ICelestialObjectRenderer;
import stellarium.stellars.display.DisplayElement;
import stellarium.stellars.display.IDisplayRenderCache;

public class DisplayHorCoord extends DisplayElement {

	public boolean displayEnabled;
	public int displayFrag;
	public double displayAlpha;
	public double[] displayHeightColor;
	public double[] displayAzimuthColor;
	
	private Property propDisplayEnabled, propDisplayAlpha, propDisplayFrag;
	private Property propDisplayLatitudeColor, propDisplayLongitudeColor;
	
	@Override
	public void setupConfig(Configuration config, String category) {
		config.setCategoryComment(category, "Configurations for Display of Horizontal Coordinate Grid.");
		config.setCategoryLanguageKey(category, "config.category.display.horcoord");
		config.setCategoryRequiresMcRestart(category, false);
		
		List<String> propNameList = Lists.newArrayList();
		
        propDisplayEnabled=config.get(category, "Display_Enabled", false);
        propDisplayEnabled.comment="Set to true to enable display of horizontal coordinates.";
        propDisplayEnabled.setRequiresMcRestart(false);
        propDisplayEnabled.setLanguageKey("config.property.display.enabled");
        propNameList.add(propDisplayEnabled.getName());
        
        propDisplayAlpha=config.get(category, "Display_Alpha", 0.2);
        propDisplayAlpha.comment="Alpha(Brightness) of the display.";
        propDisplayAlpha.setRequiresMcRestart(false);
        propDisplayAlpha.setLanguageKey("config.property.display.alpha");
        propNameList.add(propDisplayAlpha.getName());
        
        propDisplayFrag=config.get(category, "Display_Fragments_Number", 32);
        propDisplayFrag.comment="Number of fragments of display grids in direction of height.";
        propDisplayFrag.setRequiresMcRestart(false);
        propDisplayFrag.setLanguageKey("config.property.display.horcoord.fragments");
        propNameList.add(propDisplayFrag.getName());
        
        propDisplayLatitudeColor=config.get(category, "Display_Height_Color", new double[] {0.0, 0.5, 0.5});
        propDisplayLatitudeColor.comment = "Color factor for height, the grid tends to have this color when height gets bigger.";
        propDisplayLatitudeColor.setIsListLengthFixed(true);
        propDisplayLatitudeColor.setRequiresMcRestart(false);
        propDisplayLatitudeColor.setLanguageKey("config.property.horcoord.display.color.height");
        propNameList.add(propDisplayLatitudeColor.getName());

        propDisplayLongitudeColor=config.get(category, "Display_Azimuth_Color", new double[] {0.5, 0.0, 0.5});
        propDisplayLatitudeColor.comment = "Color factor for azimuth(horizontal position), the grid tends to have this color when azimuth gets bigger.";
        propDisplayLongitudeColor.setIsListLengthFixed(true);
        propDisplayLongitudeColor.setRequiresMcRestart(false);
        propDisplayLongitudeColor.setLanguageKey("config.property.horcoord.display.color.azimuth");
        propNameList.add(propDisplayLongitudeColor.getName());
        
        config.setCategoryPropertyOrder(category, propNameList);
	}

	@Override
	public void loadFromConfig(Configuration config, String category) {
		this.displayEnabled = propDisplayEnabled.getBoolean();
		this.displayAlpha = propDisplayAlpha.getDouble();
		this.displayFrag = propDisplayFrag.getInt();
		this.displayHeightColor = propDisplayLatitudeColor.getDoubleList();
		this.displayAzimuthColor = propDisplayLongitudeColor.getDoubleList();
	}

	@Override
	public void saveToConfig(Configuration config, String category) {
		// TODO save to configuration; not done till now		
	}
	
	@Override
	public String getID() {
		return "Horizontal_Coordinate_Grid";
	}

	@Override
	public DisplayElement copy() {
		DisplayHorCoord copied = new DisplayHorCoord();
		copied.displayAlpha = this.displayAlpha;
		copied.displayEnabled = this.displayEnabled;
		copied.displayFrag = this.displayFrag;
		copied.displayHeightColor = this.displayHeightColor;
		copied.displayAzimuthColor = this.displayAzimuthColor;
		return copied;
	}

	@Override
	public IDisplayRenderCache generateCache() {
		return new DisplayHorCoordCache();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ICelestialObjectRenderer getRenderer() {
		return new DisplayHorCoordRenderer();
	}

}
