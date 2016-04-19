package stellarium.stellars.layer;

import java.util.List;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

import stellarapi.api.ICelestialCoordinate;
import stellarapi.api.ISkyEffect;
import stellarapi.api.lib.config.IConfigHandler;
import stellarapi.api.lib.config.INBTConfig;
import stellarapi.api.optics.IViewScope;
import stellarium.StellarSky;
import stellarium.client.ClientSettings;
import stellarium.common.CommonSettings;
import stellarium.stellars.view.IStellarSkySet;

public class CelestialManager {
	
	private List<ICelestialLayer> layers = Lists.newArrayList();
	private boolean isRemote;
	
	public CelestialManager(boolean isRemote) {
		this.isRemote = isRemote;
		
		CelestialLayerRegistry registry = CelestialLayerRegistry.getInstance();
		
		registry.composeCommonLayer(this.layers);
	}
	
	public List<ICelestialLayer> getLayers() {
		return this.layers;
	}
	
	public void initializeClient(ClientSettings settings) {
		StellarSky.logger.info("Initializing Celestial Layers with Client Settings...");
		String layerName = null;
		try {
			for(ICelestialLayer layer : this.layers) {
				layerName = CelestialLayerRegistry.getInstance().getConfigName(layer);
				layer.initializeClient(true, layerName != null? settings.getSubConfig(layerName) : null);
			}
		} catch(Exception exception) {
	    	StellarSky.logger.fatal("Failed to initialize Celestial Layer %s by Exception %s",
	    			layerName, exception.toString());
			Throwables.propagate(exception);
		}
    	StellarSky.logger.info("Successfully initialized Celestial Layers with Client Settings!");
	}
	
	public void initializeCommon(CommonSettings settings) {
		StellarSky.logger.info("Initializing Celestial Layers with Common Settings...");
		String layerName = null;
		try {
			for(ICelestialLayer layer : this.layers) {
				layerName = CelestialLayerRegistry.getInstance().getConfigName(layer);
				layer.initializeCommon(false, layerName != null? settings.getSubConfig(layerName) : null);
			}
		} catch(Exception exception) {
	    	StellarSky.logger.fatal("Failed to initialize Celestial Layer %s by Exception %s",
	    			layerName, exception.toString());
			Throwables.propagate(exception);
		}
    	StellarSky.logger.info("Successfully initialized Celestial Layers with Common Settings!");
	}
	
	public void reloadClientSettings(ClientSettings settings) {
		StellarSky.logger.info("Reloading Client Settings...");
		String layerName = null;
		for(ICelestialLayer<? extends INBTConfig, ? extends IConfigHandler> layer : this.layers)
			for(CelestialObject object : layer.getObjectList())
				if(object.getRenderId() != -1)
				{
					layerName = CelestialLayerRegistry.getInstance().getConfigName(layer);
					object.getRenderCache().initialize(settings, layerName != null? settings.getSubConfig(layerName) : null);
				}
		StellarSky.logger.info("Client Settings reloaded.");
	}
	
	public void update(double year) {
		for(ICelestialLayer layer : this.layers)
			layer.updateLayer(year);
	}
	
	public void updateClient(ClientSettings settings,
			ICelestialCoordinate coordinate, ISkyEffect sky, IViewScope scope) {
		String layerName = null;
		
		for(ICelestialLayer<? extends INBTConfig, ? extends IConfigHandler> layer : this.layers)
			for(CelestialObject object : layer.getObjectList()) {
				if(object.getRenderId() != -1) {
					layerName = CelestialLayerRegistry.getInstance().getConfigName(layer);
					object.getRenderCache().updateCache(settings, layerName != null? settings.getSubConfig(layerName) : null,
							object, coordinate, sky, scope);
				}
			}
	}

}
