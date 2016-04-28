package stellarium.stellars.display;

import stellarium.client.ClientSettings;
import stellarium.stellars.layer.IRenderCache;
import stellarium.stellars.layer.StellarCacheInfo;

public class WrappedDisplayRenderCache implements IRenderCache<DisplayElement, DisplaySettings> {

	IDisplayRenderCache internal;
	private String id;
	private LayerDisplay.DisplayDelegate delegate;

	public WrappedDisplayRenderCache(LayerDisplay.DisplayDelegate delegate) {
		this.delegate = delegate;
		this.internal = delegate.type.generateCache();
		this.id = delegate.type.getName();
	}

	@Override
	public void initialize(ClientSettings settings, DisplaySettings specificSettings, DisplayElement object) {
		internal.initialize(settings, (DisplayElementSettings)specificSettings.getSubConfig(this.id));
	}

	@Override
	public void updateCache(ClientSettings settings, DisplaySettings specificSettings, DisplayElement object,
			StellarCacheInfo info) {
		internal.updateCache(settings, (DisplayElementSettings)specificSettings.getSubConfig(this.id), info);
	}

	@Override
	public int getRenderId() {
		return delegate.renderId;
	}

}
