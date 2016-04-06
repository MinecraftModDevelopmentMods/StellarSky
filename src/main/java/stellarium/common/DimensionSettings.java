package stellarium.common;

import java.util.Set;

import com.google.common.collect.Sets;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import stellarium.config.HierarchicalConfig;
import stellarium.stellars.view.PerDimensionSettings;

public class DimensionSettings extends HierarchicalConfig {
	
	private Property dimensionApplied;
	
	@Override
	public void setupConfig(Configuration config, String category) {
		config.setCategoryComment(category, "Configurations for Dimension Settings.");
		config.setCategoryLanguageKey(category, "config.category.dimension");
		config.setCategoryRequiresWorldRestart(category, true);
		
		this.dimensionApplied = config.get(category, "Applied_Dimensions",
				new String[] {"Overworld", "The End"});
		dimensionApplied.comment = "Dimensions which will get applied the stellar sky settings.";
		dimensionApplied.setRequiresWorldRestart(true);
		dimensionApplied.setLanguageKey("config.property.dimension.applied");
		
		Set<String> dimApplied = Sets.newHashSet(dimensionApplied.getStringList());
		
		for(String dimName : dimensionApplied.getStringList())
			if(!this.hasSubConfig(dimName))
				this.putSubConfig(dimName, new PerDimensionSettings(dimName));
		
		
		for(String dimName : this.getKeySet())
			if(!dimApplied.contains(dimName))
				this.removeSubConfig(dimName);
		
		super.setupConfig(config, category);
	}

	@Override
	public void loadFromConfig(Configuration config, String category) {
		super.loadFromConfig(config, category);
	}

}
