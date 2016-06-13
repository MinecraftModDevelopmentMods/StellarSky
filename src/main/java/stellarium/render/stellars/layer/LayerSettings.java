package stellarium.render.stellars.layer;

import stellarapi.api.lib.config.IConfigHandler;
import stellarium.stellars.layer.StellarObject;

public class LayerSettings {
	
	private ObjectSettings cachedObjectSettings;

	public LayerSettings(IConfigHandler settings) {
		this.cachedObjectSettings = new ObjectSettings(settings);
	}

	public ObjectSettings getSettingsFor(StellarObject object) {
		cachedObjectSettings.setObject(object);
		return this.cachedObjectSettings;
	}

}
