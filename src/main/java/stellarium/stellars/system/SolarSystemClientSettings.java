package stellarium.stellars.system;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import stellarapi.api.lib.config.IConfigHandler;

public class SolarSystemClientSettings implements IConfigHandler {
	
	public int imgFrac;
	
	private Property propMoonFrac;

	@Override
	public void setupConfig(Configuration config, String category) {
		config.setCategoryComment(category, "Configurations for solar system.");
		config.setCategoryLanguageKey(category, "config.category.solarsystem");
		config.setCategoryRequiresWorldRestart(category, false);
		
		List<String> propNameList = Lists.newArrayList();
        
        propMoonFrac=config.get(category, "Moon_Fragments_Number", 16);
        propMoonFrac.comment="Surfaces of Sun & Moon is drawn with fragments\n" +
        		"Less fragments will increase FPS, but the moon will become more defective";
        propMoonFrac.setRequiresMcRestart(false);
        propMoonFrac.setLanguageKey("config.property.client.moonfrac");
        propMoonFrac.setMinValue(4).setMaxValue(64);
        propNameList.add(propMoonFrac.getName());
        
        config.setCategoryPropertyOrder(category, propNameList);
	}

	@Override
	public void loadFromConfig(Configuration config, String category) {
		this.imgFrac = propMoonFrac.getInt();
	}

	@Override
	public void saveToConfig(Configuration config, String category) { }

}
