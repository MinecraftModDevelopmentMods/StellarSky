package stellarium.world.landscape;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import stellarapi.api.lib.config.IConfigHandler;

public class LandscapeClientSettings implements IConfigHandler {

	public int displayFrag;
	
	private Property propDisplayFrag;

	public static String KEY = "landscape";

	@Override
	public void setupConfig(Configuration config, String category) {
		config.setCategoryComment(category, "Configurations for Landscape.");
		config.setCategoryLanguageKey(category, "config.category.landscape");
		config.setCategoryRequiresMcRestart(category, false);
		
		List<String> propNameList = Lists.newArrayList();
        
        propDisplayFrag=config.get(category, "Landscape_Fragments_Number", 16);
        propDisplayFrag.comment="Number of fragments of landscape in direction of height."
        		+ "Less fragments will increase FPS, but the landscape will become more defective";
        propDisplayFrag.setRequiresMcRestart(false);
        propDisplayFrag.setLanguageKey("config.property.landscape.fragments");
        propDisplayFrag.setMinValue(4).setMaxValue(64);
        propNameList.add(propDisplayFrag.getName());
        
        config.setCategoryPropertyOrder(category, propNameList);
	}

	@Override
	public void loadFromConfig(Configuration config, String category) {
		this.displayFrag = propDisplayFrag.getInt();
	}

	@Override
	public void saveToConfig(Configuration config, String category) { }

}
