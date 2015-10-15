package stellarium.config;

import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraftforge.common.config.Configuration;

public class ConfigManager {
	
	private Configuration config;
	private Map<String, IConfigHandler> handlerMap = Maps.newHashMap();
	
	public void setConfigInstance(Configuration config) {
		this.config = config;
	}
	
	public void register(String category, IConfigHandler cfgHandler) {
		handlerMap.put(category, cfgHandler);
	}
	
	public void onSyncConfig(boolean loadFromFile, boolean isLoadPhase) {
		if(loadFromFile)
			config.load();
		
		for(Map.Entry<String, IConfigHandler> entry : handlerMap.entrySet()) {
			entry.getValue().setupConfig(config, entry.getKey());
		}
		
		if(isLoadPhase)
		{
			for(Map.Entry<String, IConfigHandler> entry : handlerMap.entrySet()) {
				entry.getValue().loadFromConfig(config, entry.getKey());
			}
		}
		
		if(config.hasChanged())
			config.save();
	}
	
	  /**
	   * load the configuration values from the configuration file
	   */
	  public void syncFromFile()
	  {
		  onSyncConfig(true, true);
	  }

	  /**
	   * save the GUI-altered values to disk
	   */
	  public void syncFromGUI()
	  {
		  onSyncConfig(false, true);
	  }

	  /**
	   * save the variables (fields) to disk
	   */
	  public void syncFromFields()
	  {
		  onSyncConfig(false, false);
	  }

}
