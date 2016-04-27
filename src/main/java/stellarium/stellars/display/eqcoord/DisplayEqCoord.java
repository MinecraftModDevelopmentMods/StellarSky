package stellarium.stellars.display.eqcoord;

import java.util.List;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import stellarium.render.ICelestialObjectRenderer;
import stellarium.stellars.display.DisplayElement;
import stellarium.stellars.display.IDisplayRenderCache;

public class DisplayEqCoord extends DisplayElement {

	public boolean displayEnabled;
	public int displayFrag;
	public double displayAlpha;
	public double[] displayBaseColor;
	public double[] displayDecColor;
	public double[] displayRAColor;
	
	private Property propDisplayEnabled, propDisplayAlpha, propDisplayFrag;
	private Property propDisplayBaseColor, propDisplayDecColor, propDisplayRAColor;
	
	@Override
	public void setupConfig(Configuration config, String category) {
		config.setCategoryComment(category, "Configurations for Display of Equatorial Coordinate Grid.");
		config.setCategoryLanguageKey(category, "config.category.display.eqcoord");
		config.setCategoryRequiresMcRestart(category, false);
		
		List<String> propNameList = Lists.newArrayList();
		
        propDisplayEnabled=config.get(category, "Display_Enabled", false);
        propDisplayEnabled.comment="Set to true to enable display of equatorial coordinates.";
        propDisplayEnabled.setRequiresMcRestart(false);
        propDisplayEnabled.setLanguageKey("config.property.display.enabled");
        propNameList.add(propDisplayEnabled.getName());
        
        propDisplayAlpha=config.get(category, "Display_Alpha", 0.05);
        propDisplayAlpha.comment="Alpha(Brightness) of the display.";
        propDisplayAlpha.setRequiresMcRestart(false);
        propDisplayAlpha.setLanguageKey("config.property.display.alpha");
        propNameList.add(propDisplayAlpha.getName());
        
        propDisplayFrag=config.get(category, "Display_Fragments_Number", 16);
        propDisplayFrag.comment="Number of fragments of display grids in horizontal direction.";
        propDisplayFrag.setRequiresMcRestart(false);
        propDisplayFrag.setLanguageKey("config.property.display.eqcoord.fragments");
        propNameList.add(propDisplayFrag.getName());
        
        propDisplayBaseColor=config.get(category, "Display_Base_Color", new double[] {0.5, 0.25, 0.25});
        propDisplayBaseColor.comment = "Base color factor, the grid tends to have this color as base.";
        propDisplayBaseColor.setIsListLengthFixed(true);
        propDisplayBaseColor.setRequiresMcRestart(false);
        propDisplayBaseColor.setLanguageKey("config.property.display.eqcoord.color.base");
        propNameList.add(propDisplayBaseColor.getName());
        
        propDisplayDecColor=config.get(category, "Display_Declination_Color", new double[] {0.0, 0.5, 0.5});
        propDisplayDecColor.comment = "Color factor for declination, the grid tends to have this color when declination gets bigger.";
        propDisplayDecColor.setIsListLengthFixed(true);
        propDisplayDecColor.setRequiresMcRestart(false);
        propDisplayDecColor.setLanguageKey("config.property.display.eqcoord.color.dec");
        propNameList.add(propDisplayDecColor.getName());

        propDisplayRAColor=config.get(category, "Display_Right_Ascension_Color", new double[] {1.0, 0.0, 0.0});
        propDisplayRAColor.comment = "Color factor for right ascension, the grid tends to have this color when right ascension gets bigger.";
        propDisplayRAColor.setIsListLengthFixed(true);
        propDisplayRAColor.setRequiresMcRestart(false);
        propDisplayRAColor.setLanguageKey("config.property.display.eqcoord.color.ra");
        propNameList.add(propDisplayRAColor.getName());
        
        config.setCategoryPropertyOrder(category, propNameList);
	}

	@Override
	public void loadFromConfig(Configuration config, String category) {
		this.displayEnabled = propDisplayEnabled.getBoolean();
		this.displayAlpha = propDisplayAlpha.getDouble();
		this.displayFrag = propDisplayFrag.getInt();
		this.displayBaseColor = propDisplayBaseColor.getDoubleList();
		this.displayDecColor = propDisplayDecColor.getDoubleList();
		this.displayRAColor = propDisplayRAColor.getDoubleList();
	}

	@Override
	public void saveToConfig(Configuration config, String category) {
		// TODO save to configuration; not done till now		
	}
	
	@Override
	public String getID() {
		return "Equatorial_Coordinate_Grid";
	}

	@Override
	public IDisplayRenderCache generateCache() {
		return new DisplayEqCoordCache();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ICelestialObjectRenderer getRenderer() {
		return new DisplayEqCoordRenderer();
	}

}
