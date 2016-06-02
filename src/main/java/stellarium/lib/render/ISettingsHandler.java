package stellarium.lib.render;

import stellarapi.api.lib.config.IConfigHandler;

public interface ISettingsHandler<Config extends IConfigHandler> {

	/**
	 * Sets up the configurations,
	 * and provide the child configurations for
	 * settings of subModel/subRenderers.
	 * */
	public void setupSettings(Config config);

}
