package stellarium.display;

import stellarium.client.ClientSettings;

public interface IDisplayCache<Settings extends PerDisplaySettings> {

	public void initialize(ClientSettings settings, Settings specificSettings);
	
	public void updateCache(DisplayCacheInfo info);

}