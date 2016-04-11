package stellarium.stellars.layer;

import java.io.IOException;
import java.util.List;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

import sciapi.api.value.euclidian.EVector;
import stellarium.StellarSky;
import stellarium.client.ClientSettings;
import stellarium.common.CommonSettings;
import stellarium.config.IConfigHandler;
import stellarium.stellars.view.IStellarViewpoint;

public class CelestialManager {
	
	private List<ICelestialLayerCommon> commonLayers = Lists.newArrayList();
	private List<ICelestialLayer> layers = Lists.newArrayList();
	private boolean isRemote;
	
	public CelestialManager(boolean isRemote) {
		this.isRemote = isRemote;
		
		CelestialLayerRegistry registry = CelestialLayerRegistry.getInstance();
		
		if(isRemote)
			registry.composeClientLayer(this.layers, true);
		registry.composeCommonLayer(this.commonLayers, false);
		
		layers.addAll(this.commonLayers);
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
				layer.initialize(true, layerName != null? settings.getSubConfig(layerName) : null);
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
			for(ICelestialLayerCommon layer : this.commonLayers) {
				layerName = CelestialLayerRegistry.getInstance().getConfigName(layer);
				layer.initializeCommon(true, layerName != null? settings.getSubConfig(layerName) : null);
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
		for(ICelestialLayer<? extends IConfigHandler> layer : this.layers)
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
	
	public void updateClient(ClientSettings settings, IStellarViewpoint viewpoint) {
		String layerName = null;
		
		for(ICelestialLayer<? extends IConfigHandler> layer : this.layers)
			for(CelestialObject object : layer.getObjectList()) {
				if(object.getRenderId() != -1) {
					layerName = CelestialLayerRegistry.getInstance().getConfigName(layer);
					object.getRenderCache().updateCache(settings, layerName != null? settings.getSubConfig(layerName) : null,
							object, viewpoint);
				}
			}
	}
	
	public EVector getSunEcRPos() {
		for(ICelestialLayerCommon layer : this.commonLayers)
			if(layer.provideSun())
				return layer.getSunEcRPos();
		
		return new EVector(3);
	}
	
	public EVector getMoonEcRPos() {
		for(ICelestialLayerCommon layer : this.commonLayers)
			if(layer.provideMoon())
				return layer.getMoonEcRPos();
		
		return new EVector(3);
	}
	
	public double[] getMoonFactors() {
		for(ICelestialLayerCommon layer : this.commonLayers)
			if(layer.provideMoon())
				return layer.getMoonFactors();
		
		return new double[3];
	}

}
