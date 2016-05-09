package stellarium.stellars.display;

import net.minecraftforge.common.config.Configuration;
import stellarapi.api.lib.config.SimpleHierarchicalConfig;

public class DisplayOverallSettings extends SimpleHierarchicalConfig {
	
	private LayerDisplay display;
	
	public DisplayOverallSettings(LayerDisplay display) {
		this.display = display;
		
		for(DisplayRegistry.Delegate delegate : display.getDelegates()) {
			this.putSubConfig(delegate.getType().getName(), delegate.getType().generateSettings());
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
