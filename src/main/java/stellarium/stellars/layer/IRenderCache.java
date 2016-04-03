package stellarium.stellars.layer;

import stellarium.client.ClientSettings;
import stellarium.stellars.view.IStellarViewpoint;

public interface IRenderCache<T extends CelestialObject> {
	
	public void initialize(ClientSettings settings);
	public void updateCache(ClientSettings settings, T object, IStellarViewpoint viewpoint);
	
}
