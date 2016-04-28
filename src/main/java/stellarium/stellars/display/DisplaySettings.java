package stellarium.stellars.display;

import java.util.List;

import net.minecraftforge.common.config.Configuration;
import stellarapi.api.lib.config.SimpleHierarchicalConfig;

public class DisplaySettings extends SimpleHierarchicalConfig {
	
	private LayerDisplay display;
	
	public DisplaySettings(LayerDisplay display) {
		this.display = display;
		
		for(LayerDisplay.DisplayDelegate delegate : display.displayElementDelegates) {
			this.putSubConfig(delegate.type.getName(), delegate.type.generateSettings());
		}
	}

	@Override
	public void setupConfig(Configuration config, String category) {
		config.setCategoryComment(category, "Configurations for additional display.");
		config.setCategoryLanguageKey(category, "config.category.display");
		config.setCategoryRequiresMcRestart(category, false);
		
		super.setupConfig(config, category);
	}

}
