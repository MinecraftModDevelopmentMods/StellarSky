package stellarium.render.sky;

import net.minecraft.world.World;
import stellarium.client.ClientSettings;
import stellarium.lib.render.ISettingsHandler;
import stellarium.render.state.EnumUpdateState;
import stellarium.render.state.InitiationState;
import stellarium.render.stellars.StellarSettingsHandler;

public class SkySettingsHandler implements ISettingsHandler<ClientSettings> {

	private StellarSettingsHandler stellarCfgHandler = new StellarSettingsHandler();
	private ClientSettings config;

	@Override
	public void setupSettings(ClientSettings config) {
		stellarCfgHandler.setupSettings(config);
		this.config = config;
	}

	public StellarSettingsHandler getStellarSettings() {
		return this.stellarCfgHandler;
	}

	public boolean checkDirty() {
		return config.checkDirty();
	}
}
