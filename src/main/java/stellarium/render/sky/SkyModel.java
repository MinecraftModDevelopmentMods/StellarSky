package stellarium.render.sky;

import stellarium.client.ClientSettings;
import stellarium.lib.render.IRenderModel;
import stellarium.render.stellars.StellarModel;

public class SkyModel implements IRenderModel<SkySettingsHandler, SkyUpdateInfo> {

	private StellarModel model;
	
	@Override
	public void initialize(SkySettingsHandler settings) {
		model.initialize(settings.getStellarSettings());
	}

	@Override
	public void update(SkySettingsHandler settings, SkyUpdateInfo update) {
		model.update(settings.getStellarSettings(), update.getStellarUpdates());
	}

	public Object getDisplayModel() {
		// TODO display model
		return null;
	}

	public StellarModel getStellarModel() {
		return this.model;
	}

	public Object getLandscapeModel() {
		// TODO landscape model
		return null;
	}

}
