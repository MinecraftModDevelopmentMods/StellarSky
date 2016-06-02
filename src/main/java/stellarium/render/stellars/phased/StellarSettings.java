package stellarium.render.stellars.phased;

import stellarapi.api.lib.config.IConfigHandler;
import stellarium.client.ClientSettings;
import stellarium.lib.render.ISettingsHandler;
import stellarium.render.state.InitiationState;
import stellarium.stellars.layer.StellarLayerRegistry;

public class StellarSettings implements ISettingsHandler<ClientSettings> {
	
	private ClientSettings settings;

	@Override
	public void setupSettings(ClientSettings config) {
		StellarLayerRegistry.getInstance().composeSettings(config);
		this.settings = config;
	}
	
	public IConfigHandler getSubConfig(String configName) {
		return settings.getSubConfig(configName);
	}

}
