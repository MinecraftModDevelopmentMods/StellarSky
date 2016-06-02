package stellarium.render.stellars.layer;

import stellarapi.api.lib.config.IConfigHandler;
import stellarium.stellars.layer.StellarObject;

public class ObjectSettings {
	
	private IConfigHandler settings;
	private StellarObject object;
	
	public ObjectSettings(IConfigHandler settings) {
		this.settings = settings;
	}
	
	public void setObject(StellarObject object) {
		this.object = object;
	}
	
	public StellarObject getObject() {
		return this.object;
	}
	
	public <T extends IConfigHandler> T getLayerSettings() {
		return (T) this.settings;
	}

}
