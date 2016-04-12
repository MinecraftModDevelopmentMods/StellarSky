package stellarium.config;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraftforge.common.config.Configuration;
import stellarium.config.property.ConfigProperty;

public class SimpleConfigHandler implements IConfigHandler {
	
	protected List<ConfigProperty> listProperties = Lists.newArrayList();
	protected Map<String, ConfigProperty> mapProperties = Maps.newHashMap();
	
	public void addConfigProperty(ConfigProperty property) {
		listProperties.add(property);
		mapProperties.put(property.getConfigName(), property);
	}

	@Override
	public void setupConfig(Configuration config, String category) {
		List<String> configNames = Lists.newArrayList();
		for(ConfigProperty property : this.listProperties)
		{
			property.setupConfiguration(config, category);
			configNames.add(property.getConfigName());
		}
		
		config.setCategoryPropertyOrder(category, configNames);
	}

	@Override
	public void loadFromConfig(Configuration config, String category) {
		for(ConfigProperty property : this.listProperties)
			property.loadFromConfig();
	}

	@Override
	public void saveToConfig(Configuration config, String category) {
		for(ConfigProperty property : this.listProperties)
			property.saveToConfig();
	}

}
