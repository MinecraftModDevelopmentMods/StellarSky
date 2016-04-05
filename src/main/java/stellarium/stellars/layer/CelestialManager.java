package stellarium.stellars.layer;

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
	
	private List<ICelestialLayer> clientLayers = Lists.newArrayList();
	private List<ICelestialLayer> commonLayers = Lists.newArrayList();
	private List<ICelestialLayer> layers = Lists.newArrayList();
	private boolean isRemote;
	
	public CelestialManager(boolean isRemote) {
		this.isRemote = isRemote;
		
		CelestialLayerRegistry registry = CelestialLayerRegistry.getInstance();
		
		if(isRemote)
			registry.composeLayer(this.clientLayers, true);
		registry.composeLayer(this.commonLayers, false);
		
		layers.addAll(this.clientLayers);
		layers.addAll(this.commonLayers);
	}
	
	public List<ICelestialLayer> getLayers() {
		return this.layers;
	}
	
	public void initializeClient(ClientSettings settings) {
		StellarSky.logger.info("Initializing Client Celestial Layers...");
		String layerName = null;
		try {
			for(ICelestialLayer layer : this.clientLayers) {
				layerName = CelestialLayerRegistry.getInstance().getConfigName(layer);
				layer.initialize(true, layerName != null? settings.getSubConfig(layerName) : null);
			}
		} catch(Exception exception) {
	    	StellarSky.logger.fatal("Failed to load Client Celestial Layer %s by Exception %s",
	    			layerName, exception.toString());
			Throwables.propagate(exception);
		}
    	StellarSky.logger.info("Successfully initialized Client Celestial Layers!");
	}
	
	public void initializeCommon(CommonSettings settings) {
		StellarSky.logger.info("Initializing Common Celestial Layers...");
		String layerName = null;
		try {
			for(ICelestialLayer layer : this.commonLayers) {
				layerName = CelestialLayerRegistry.getInstance().getConfigName(layer);
				layer.initialize(true, layerName != null? settings.getSubConfig(layerName) : null);
			}
		} catch(Exception exception) {
	    	StellarSky.logger.fatal("Failed to load Common Celestial Layer %s by Exception %s",
	    			layerName, exception.toString());
			Throwables.propagate(exception);
		}
    	StellarSky.logger.info("Successfully initialized Common Celestial Layers!");
	}
	
	public void reloadClientSettings(ClientSettings settings) {
		StellarSky.logger.info("Reloading Client Settings...");
		for(ICelestialLayer<? extends IConfigHandler> layer : this.layers)
			for(CelestialObject object : layer.getObjectList())
				if(object.getRenderId() != -1)
					object.getRenderCache().initialize(settings);
		StellarSky.logger.info("Client Settings reloaded.");
	}
	
	public void update(double year) {
		for(ICelestialLayer layer : this.layers)
			layer.updateLayer(year);
	}
	
	public void updateClient(ClientSettings settings, IStellarViewpoint viewpoint) {
		for(ICelestialLayer<? extends IConfigHandler> layer : this.layers)
			for(CelestialObject object : layer.getObjectList()) {
				if(object.getRenderId() != -1)
					object.getRenderCache().updateCache(settings, object, viewpoint);
			}
	}
	
	public EVector getSunEcRPos() {
		for(ICelestialLayer layer : this.commonLayers)
			if(layer instanceof ICelestialLayerCommon)  {
				ICelestialLayerCommon commonLayer = (ICelestialLayerCommon) layer;
				if(commonLayer.provideSun())
					return commonLayer.getSunEcRPos();
			}
		
		return new EVector(3);
	}
	
	public EVector getMoonEcRPos() {
		for(ICelestialLayer layer : this.commonLayers)
			if(layer instanceof ICelestialLayerCommon)  {
				ICelestialLayerCommon commonLayer = (ICelestialLayerCommon) layer;
				if(commonLayer.provideMoon())
					return commonLayer.getMoonEcRPos();
			}
		
		return new EVector(3);
	}
	
	public double[] getMoonFactors() {
		for(ICelestialLayer layer : this.commonLayers)
			if(layer instanceof ICelestialLayerCommon)  {
				ICelestialLayerCommon commonLayer = (ICelestialLayerCommon) layer;
				if(commonLayer.provideMoon())
					return commonLayer.getMoonFactors();
			}
		
		return new double[3];
	}

}
