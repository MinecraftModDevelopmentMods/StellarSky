package stellarium.display;

import stellarium.client.ClientSettings;
import stellarium.stellars.layer.StellarCacheInfo;

public interface IDisplayCache<Settings extends PerDisplaySettings> {

	public void initialize(ClientSettings settings, Settings specificSettings);
	
	public void updateCache(ClientSettings settings, Settings specificSettings,
			DisplayCacheInfo info);

}