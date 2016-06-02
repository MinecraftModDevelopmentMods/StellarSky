package stellarium.render.stellars;

import stellarium.lib.render.IRenderModel;
import stellarium.render.stellars.atmosphere.AtmosphereModel;
import stellarium.render.stellars.phased.StellarRenderModel;

public class StellarModel implements IRenderModel<StellarSettingsHandler, AtmStellarUpdateInfo> {

	private StellarRenderModel layersModel;
	private AtmosphereModel atmModel;
	
	public StellarRenderModel getStellarModel() {
		return this.layersModel;
	}

	public AtmosphereModel getAtmosphereModel() {
		return this.atmModel;
	}

	@Override
	public void initialize(StellarSettingsHandler settings) {
		layersModel.initialize(settings.getStellarSettings());
	}

	@Override
	public void update(StellarSettingsHandler settings, AtmStellarUpdateInfo update) {
		layersModel.update(settings.getStellarSettings(), update);
	}

}
