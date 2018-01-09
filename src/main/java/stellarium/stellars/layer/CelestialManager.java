package stellarium.stellars.layer;

import java.util.List;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import stellarium.StellarSky;
import stellarium.client.ClientSettings;
import stellarium.common.ServerSettings;

public class CelestialManager {

	private boolean isRemote, commonInitialized;
	private List<StellarObjectContainer> layers = Lists.newArrayList();
	
	private CelestialManager() { }
	
	public CelestialManager(boolean isRemote) {
		this.isRemote = isRemote;
		
		StellarLayerRegistry registry = StellarLayerRegistry.getInstance();
		registry.composeLayer(isRemote, this.layers);
	}
	
	public List<StellarObjectContainer> getLayers() {
		return this.layers;
	}
	
	public void initializeClient(ClientSettings settings) {		
		StellarSky.INSTANCE.getLogger().info("Initializing Celestial Layers with Client Settings...");
		String layerName = null;
		try {
			for(StellarObjectContainer layer : this.layers) {
				layerName = layer.getConfigName();
				layer.getType().initializeClient(layerName != null? settings.getSubConfig(layerName) : null, layer);
			}
		} catch(Exception exception) {
	    	StellarSky.INSTANCE.getLogger().fatal("Failed to initialize Celestial Layer %s by Exception %s",
	    			layerName, exception.toString());
			Throwables.propagate(exception);
		}
    	StellarSky.INSTANCE.getLogger().info("Successfully initialized Celestial Layers with Client Settings!");
	}
	
	public void initializeCommon(ServerSettings settings) {
		StellarSky.INSTANCE.getLogger().info("Initializing Celestial Layers with Common Settings...");
		String layerName = null;
		try {
			for(StellarObjectContainer layer : this.layers) {
				layerName = layer.getConfigName();
				layer.getType().initializeCommon(layerName != null? settings.getSubConfig(layerName) : null, layer);
			}
		} catch(Exception exception) {
	    	StellarSky.INSTANCE.getLogger().fatal("Failed to initialize Celestial Layer %s by Exception %s",
	    			layerName, exception.toString());
			Throwables.propagate(exception);
		}
    	StellarSky.INSTANCE.getLogger().info("Successfully initialized Celestial Layers with Common Settings!");
    	this.commonInitialized = true;
	}
	
	public void update(double year) {
		for(StellarObjectContainer layer : this.layers)
			layer.getType().updateLayer(layer, year);
	}


	public CelestialManager copyFromClient() {
		CelestialManager copied = new CelestialManager();
		copied.isRemote = this.isRemote;
		copied.layers = Lists.newArrayList(
				Iterables.transform(this.layers,
						new Function<StellarObjectContainer, StellarObjectContainer>() {
							@Override
							public StellarObjectContainer apply(StellarObjectContainer input) {
								return input.copyFromClient();
							}
				}));
		
		return copied;
	}
	
	public boolean commonInitialized() {
		return this.commonInitialized;
	}

}
