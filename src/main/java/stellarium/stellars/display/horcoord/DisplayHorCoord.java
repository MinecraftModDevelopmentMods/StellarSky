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
	public double[] displayBaseColor;
	public double[] displayHeightColor;
	public double[] displayAzimuthColor;
	
	private Property propDisplayEnabled, propDisplayAlpha, propDisplayFrag;
	private Property propDisplayBaseColor, propDisplayHeightColor, propDisplayAzimuthColor;
	
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
        
        propDisplayAlpha=config.get(category, "Display_Alpha", 0.05);
        propDisplayAlpha.comment="Alpha(Brightness) of the display.";
        propDisplayAlpha.setRequiresMcRestart(false);
        propDisplayAlpha.setLanguageKey("config.property.display.alpha");
        propNameList.add(propDisplayAlpha.getName());
        
        propDisplayFrag=config.get(category, "Display_Fragments_Number", 16);
        propDisplayFrag.comment="Number of fragments of display grids in direction of height.";
        propDisplayFrag.setRequiresMcRestart(false);
        propDisplayFrag.setLanguageKey("config.property.display.horcoord.fragments");
        propNameList.add(propDisplayFrag.getName());
        
        propDisplayBaseColor=config.get(category, "Display_Base_Color", new double[] {0.25, 0.25, 0.5});
        propDisplayBaseColor.comment = "Base color factor, the grid tends to have this color as base.";
        propDisplayBaseColor.setIsListLengthFixed(true);
        propDisplayBaseColor.setRequiresMcRestart(false);
        propDisplayBaseColor.setLanguageKey("config.property.display.horcoord.color.base");
        propNameList.add(propDisplayBaseColor.getName());
        
        propDisplayHeightColor=config.get(category, "Display_Height_Color", new double[] {0.0, 0.0, 1.0});
        propDisplayHeightColor.comment = "Color factor for height, the grid tends to have this color when height gets bigger.";
        propDisplayHeightColor.setIsListLengthFixed(true);
        propDisplayHeightColor.setRequiresMcRestart(false);
        propDisplayHeightColor.setLanguageKey("config.property.display.horcoord.color.height");
        propNameList.add(propDisplayHeightColor.getName());

        propDisplayAzimuthColor=config.get(category, "Display_Azimuth_Color", new double[] {0.5, 0.5, 0.0});
        propDisplayAzimuthColor.comment = "Color factor for azimuth(horizontal position), the grid tends to have this color when azimuth gets bigger.";
        propDisplayAzimuthColor.setIsListLengthFixed(true);
        propDisplayAzimuthColor.setRequiresMcRestart(false);
        propDisplayAzimuthColor.setLanguageKey("config.property.display.horcoord.color.azimuth");
        propNameList.add(propDisplayAzimuthColor.getName());
        
        config.setCategoryPropertyOrder(category, propNameList);
	}

	@Override
	public void loadFromConfig(Configuration config, String category) {
		this.displayEnabled = propDisplayEnabled.getBoolean();
		this.displayAlpha = propDisplayAlpha.getDouble();
		this.displayFrag = propDisplayFrag.getInt();
		this.displayBaseColor = propDisplayBaseColor.getDoubleList();
		this.displayHeightColor = propDisplayHeightColor.getDoubleList();
		this.displayAzimuthColor = propDisplayAzimuthColor.getDoubleList();
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
	public IDisplayRenderCache generateCache() {
		return new DisplayHorCoordCache();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ICelestialObjectRenderer getRenderer() {
		return new DisplayHorCoordRenderer();
	}

}
