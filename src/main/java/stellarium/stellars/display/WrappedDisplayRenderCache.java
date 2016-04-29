package stellarium.stellars.display;

import stellarium.client.ClientSettings;
import stellarium.stellars.layer.IRenderCache;
import stellarium.stellars.layer.StellarCacheInfo;

public class WrappedDisplayRenderCache<Cfg extends PerDisplaySettings,
Cache extends IDisplayRenderCache<Cfg>> implements IRenderCache<DisplayElement, DisplayOverallSettings> {

	Cache internal;
	private String id;
	private DisplayRegistry.Delegate delegate;

	public WrappedDisplayRenderCache(DisplayRegistry.Delegate<Cfg, Cache> delegate) {
		this.delegate = delegate;
		this.internal = delegate.getType().generateCache();
		this.id = delegate.getType().getName();
	}

	@Override
	public void initialize(ClientSettings settings, DisplayOverallSettings specificSettings, DisplayElement object) {
		internal.initialize(settings, (Cfg)specificSettings.getSubConfig(this.id));
	}

	@Override
	public void updateCache(ClientSettings settings, DisplayOverallSettings specificSettings, DisplayElement object,
			StellarCacheInfo info) {
		internal.updateCache(settings, (Cfg)specificSettings.getSubConfig(this.id), info);
	}

	@Override
	public int getRenderId() {
		return delegate.getRenderId();
	}

}
