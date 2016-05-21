package stellarium.stellars.milkyway;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import stellarapi.api.lib.config.IConfigHandler;

public class MilkywaySettings implements IConfigHandler {
	
	public float milkywayBrightness;
	public int imgFracMilkyway;
	
	private Property propMilkywayFrac, propMilkywayBrightness;

	@Override
	public void setupConfig(Configuration config, String category) {
		config.setCategoryComment(category, "Configurations for milky way.");
		config.setCategoryLanguageKey(category, "config.category.milkyway");
		config.setCategoryRequiresMcRestart(category, false);
		
		List<String> propNameList = Lists.newArrayList();
		
		// TODO Migrate into classification.
        propMilkywayBrightness=config.get(category, "Milkyway_Brightness", 1.5);
        propMilkywayBrightness.setComment("Brightness of milky way.\n"
        		+ "For real world it should be 1.0 or lower, but default is set to 1.5 for visual effect.");
        propMilkywayBrightness.setRequiresMcRestart(false);
        propMilkywayBrightness.setLanguageKey("config.property.client.milkywaybrightness");
        propMilkywayBrightness.setMinValue(0.0).setMaxValue(3.0);
        propNameList.add(propMilkywayBrightness.getName());
        
		propMilkywayFrac=config.get(category, "Milkyway_Fragments_Number", 16);
        propMilkywayFrac.setComment("Milky way is drawn with fragments\n" +
        		"Less fragments will increase FPS, but the milky way will become more defective");

        propMilkywayFrac.setRequiresMcRestart(false);
        propMilkywayFrac.setLanguageKey("config.property.client.milkywayfrac");
        propMilkywayFrac.setMinValue(4).setMaxValue(64);
        propNameList.add(propMilkywayFrac.getName());

        config.setCategoryPropertyOrder(category, propNameList);
	}

	@Override
	public void loadFromConfig(Configuration config, String category) {
		this.milkywayBrightness = (float) propMilkywayBrightness.getDouble();
        this.imgFracMilkyway = propMilkywayFrac.getInt();
	}

	@Override
	public void saveToConfig(Configuration config, String category) { }

}
