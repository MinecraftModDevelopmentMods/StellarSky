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

public class CelestialManager {

	private boolean isRemote;
	private List<StellarObjectContainer> layers = Lists.newArrayList();
	
	public CelestialManager(boolean isRemote) {
		this.isRemote = isRemote;
		
		StellarLayerRegistry registry = StellarLayerRegistry.getInstance();
		registry.composeLayer(isRemote, this.layers);
	}
	
	public List<StellarObjectContainer> getLayers() {
		return this.layers;
	}
	
	public void initializeClient(ClientSettings settings) {
		StellarSky.logger.info("Initializing Celestial Layers with Client Settings...");
		String layerName = null;
		try {
			for(StellarObjectContainer layer : this.layers) {
				layerName = layer.getConfigName();
				layer.getType().initializeClient(layerName != null? settings.getSubConfig(layerName) : null, layer);
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
			for(StellarObjectContainer layer : this.layers) {
				layerName = layer.getConfigName();
				layer.getType().initializeCommon(layerName != null? settings.getSubConfig(layerName) : null, layer);
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
		
		for(StellarObjectContainer layer : this.layers)
		{
			String layerName = layer.getConfigName();
			layer.reloadClientSettings(settings, layerName != null? settings.getSubConfig(layerName) : null);
		}

		StellarSky.logger.info("Client Settings reloaded.");
	}
	
	public void update(double year) {
		for(StellarObjectContainer layer : this.layers)
			layer.getType().updateLayer(layer, year);
	}
	
	public void updateClient(ClientSettings settings,
			ICelestialCoordinate coordinate, ISkyEffect sky, IViewScope scope) {		
		for(StellarObjectContainer layer : this.layers)
		{
			String layerName = layer.getConfigName();
			layer.updateClient(settings, layerName != null? settings.getSubConfig(layerName) : null,
					coordinate, sky, scope);
		}
	}

}
