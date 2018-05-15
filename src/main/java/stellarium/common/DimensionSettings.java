package stellarium.common;

import net.minecraftforge.common.config.Configuration;
import stellarapi.api.SAPIReferences;
import stellarapi.api.lib.config.HierarchicalConfig;
import stellarapi.api.world.worldset.WorldSet;
import stellarium.world.PerDimensionSettings;

public class DimensionSettings extends HierarchicalConfig {
	@Override
	public void setupConfig(Configuration config, String category) {
		config.setCategoryComment(category, "Configurations for Dimension Settings.");
		config.setCategoryLanguageKey(category, "config.category.dimension");
		config.setCategoryRequiresWorldRestart(category, true);
				
		for(WorldSet worldSet : SAPIReferences.getAllWorldSets()) {
			if(worldSet.hasSky().isFalse)
				continue;

			if(!this.hasSubConfig(worldSet.name))
				this.putSubConfig(worldSet.name, new PerDimensionSettings(worldSet));
		}

		config.getCategory(category).remove("the end");
		config.getCategory(category).remove("Applied_Dimensions");
		super.setupConfig(config, category);
	}

	@Override
	public void loadFromConfig(Configuration config, String category) {
		super.loadFromConfig(config, category);
	}

}
