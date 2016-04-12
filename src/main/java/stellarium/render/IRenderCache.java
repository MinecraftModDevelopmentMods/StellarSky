package stellarium.render;

import stellarium.client.ClientSettings;
import stellarium.config.IConfigHandler;
import stellarium.stellars.layer.CelestialObject;
import stellarium.stellars.view.IStellarViewpoint;

public interface IRenderCache<Obj extends CelestialObject, Config extends IConfigHandler> {
	
	public void initialize(ClientSettings settings, Config specificSettings);
	public void updateCache(ClientSettings settings, Config specificSettings, Obj object, IStellarViewpoint viewpoint);
	
}
