package stellarium.display;

import stellarapi.api.lib.config.SimpleHierarchicalConfig;
import stellarium.client.ClientSettings;

public interface IDisplayInjectable {
	public <Cfg extends PerDisplaySettings, Cache extends IDisplayCache<Cfg>> void injectDisplay(
			IDisplayElementType<Cfg, Cache> type, Cfg settings);

	public SimpleHierarchicalConfig getSubSettings(ClientSettings settings);
}
