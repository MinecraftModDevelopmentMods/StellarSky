package stellarium.config;

import net.minecraftforge.common.config.Configuration;

public interface IConfigHandler {
	
	/**
	 * Sets up configuration.
	 * @param config the configuration instance
	 * @param category the category to set up
	 * */
	public void setupConfig(Configuration config, String category);
	
	/**
	 * Loads from configuration.
	 * (Don't call {@link Configuration#load()} here)
	 * @param config the configuration instance
	 * @param category the category to load
	 * */
	public void loadFromConfig(Configuration config, String category);
	
	/**
	 * Save fields to configuration.
	 * (Don't call {@link Configuration#save()} here)
	 * @param config the configuration instance
	 * @param category the category to load
	 * */
	public void saveToConfig(Configuration config, String category);
}
