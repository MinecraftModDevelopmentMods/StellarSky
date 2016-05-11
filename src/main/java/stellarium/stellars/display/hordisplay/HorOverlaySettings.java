package stellarium.stellars.display.hordisplay;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import stellarium.stellars.display.PerDisplaySettings;

public class HorOverlaySettings extends PerDisplaySettings {

	public boolean displayEnabled;
	public int displayFrag;
	
	private Property propDisplayEnabled, propDisplayFrag;
	
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
        
        propDisplayFrag=config.get(category, "Display_Fragments_Number", 16);
        propDisplayFrag.comment="Number of fragments of display grids in direction of height.";
        propDisplayFrag.setRequiresMcRestart(false);
        propDisplayFrag.setLanguageKey("config.property.display.horcoord.fragments");
        propDisplayFrag.setMinValue(4).setMaxValue(64);
        propNameList.add(propDisplayFrag.getName());
        
        config.setCategoryPropertyOrder(category, propNameList);
	}

	@Override
	public void loadFromConfig(Configuration config, String category) {
		this.displayEnabled = propDisplayEnabled.getBoolean();
		this.displayFrag = propDisplayFrag.getInt();
	}

	@Override
	public void saveToConfig(Configuration config, String category) { }

}
