package stellarium.stellars.layer;

import stellarapi.api.lib.config.IConfigHandler;
import stellarium.client.ClientSettings;
import stellarium.render.atmosphere.IAtmRenderedObjects;
import stellarium.render.stellars.access.IAtmosphericChecker;
import stellarium.render.stellars.access.IStellarTessellator;
import stellarium.stellars.render.ICelestialLayerRenderer;
import stellarium.stellars.render.ICelestialObjectRenderer;
import stellarium.stellars.render.StellarRenderingRegistry;
import stellarium.view.ViewerInfo;

public class StellarLayerRenderHandler implements IAtmRenderedObjects {
	
	private CelestialManager celManager;
	private ClientSettings settings;
	
	public StellarLayerRenderHandler(CelestialManager celManager, ClientSettings settings) {
		this.celManager = celManager;
		this.settings = settings;
	}

	@Override
	public void check(ViewerInfo info, IAtmosphericChecker checker) {
		for(StellarObjectContainer layer : celManager.getLayers()) {
			String layerName = layer.getConfigName();
			layer.updateClient(this.settings, layerName != null? settings.getSubConfig(layerName) : null, info, checker);
		}
	}
}
