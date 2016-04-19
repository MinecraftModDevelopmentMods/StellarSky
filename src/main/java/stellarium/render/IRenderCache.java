package stellarium.render;

import stellarapi.api.ICelestialCoordinate;
import stellarapi.api.ISkyEffect;
import stellarapi.api.lib.config.IConfigHandler;
import stellarapi.api.optics.IViewScope;
import stellarium.client.ClientSettings;
import stellarium.stellars.layer.CelestialObject;

public interface IRenderCache<Obj extends CelestialObject, Config extends IConfigHandler> {
	
	public void initialize(ClientSettings settings, Config specificSettings);
	public void updateCache(ClientSettings settings, Config specificSettings, Obj object,
			ICelestialCoordinate coordinate, ISkyEffect sky, IViewScope scope);
	
}
