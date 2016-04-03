package stellarium.config;

import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraftforge.common.config.Configuration;

public class HierarchicalConfig implements IConfigHandler {
	
	private Map<String, IConfigHandler> subConfigs = Maps.newHashMap();
	

	public void putSubConfig(String key, IConfigHandler config) {
		subConfigs.put(key, config);
	}
	
	public boolean hasSubConfig(String key) {
		return subConfigs.containsKey(key);
	}
	
	public IConfigHandler getSubConfig(String key) {
		return subConfigs.get(key);
	}

	@Override
	public void setupConfig(Configuration config, String category) {
		for(Map.Entry<String, IConfigHandler> entry : subConfigs.entrySet())
			entry.getValue().setupConfig(config, category + Configuration.CATEGORY_SPLITTER + entry.getKey());
	}

	@Override
	public void loadFromConfig(Configuration config, String category) {
		for(Map.Entry<String, IConfigHandler> entry : subConfigs.entrySet())
			entry.getValue().loadFromConfig(config, category + Configuration.CATEGORY_SPLITTER + entry.getKey());
	}

}
