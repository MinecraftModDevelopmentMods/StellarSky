package stellarium.stellars.layer;

import stellarapi.api.lib.config.IConfigHandler;
import stellarium.client.ClientSettings;

public interface IRenderCache<Obj extends StellarObject, Config extends IConfigHandler> {
	
	public void initialize(ClientSettings settings, Config specificSettings, Obj object);
	
	public void updateCache(ClientSettings settings, Config specificSettings, Obj object, StellarCacheInfo info);
	
	public int getRenderId();
	
}
