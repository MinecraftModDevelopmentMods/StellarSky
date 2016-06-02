package stellarium.render.stellars;

import stellarium.client.ClientSettings;
import stellarium.lib.render.ISettingsHandler;
import stellarium.render.state.InitiationState;
import stellarium.render.stellars.atmosphere.AtmosphereRenderSettings;
import stellarium.render.stellars.phased.StellarSettings;

public class StellarSettingsHandler implements ISettingsHandler<ClientSettings> {
	
	private AtmosphereRenderSettings atmosphereSettings;
	private StellarSettings settings;

	@Override
	public void setupSettings(ClientSettings config) {
		settings.setupSettings(config);
		config.putSubConfig("Atmosphere", this.atmosphereSettings);
	}

	public AtmosphereRenderSettings getAtmosphereSettings() {
		return this.atmosphereSettings;
	}
	
	public StellarSettings getStellarSettings() {
		return this.settings;
	}
}
