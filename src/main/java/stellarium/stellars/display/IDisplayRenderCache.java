package stellarium.stellars.display;

import stellarium.client.ClientSettings;
import stellarium.stellars.layer.IRenderCache;
import stellarium.stellars.layer.StellarCacheInfo;

public interface IDisplayRenderCache<Settings extends PerDisplaySettings> {
	
	public void initialize(ClientSettings settings, Settings specificSettings);
	
	public void updateCache(ClientSettings settings, Settings specificSettings,
			StellarCacheInfo info);
	
}