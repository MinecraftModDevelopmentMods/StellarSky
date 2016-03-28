package stellarium.stellars.sketch;

import java.io.IOException;
import java.util.List;

import com.google.common.collect.Lists;

import stellarium.client.ClientSettings;
import stellarium.common.CommonSettings;
import stellarium.stellars.star.brstar.LayerBrStar;
import stellarium.stellars.system.LayerSolarSystem;
import stellarium.stellars.view.IStellarViewpoint;

public class CelestialManager {
	
	private List<ICelestialLayer> layers = Lists.newArrayList();
	private boolean isRemote;
	
	public CelestialManager(boolean isRemote) {
		layers.add(new LayerBrStar());
		layers.add(new LayerSolarSystem());
		this.isRemote = isRemote;
	}
	
	public List<ICelestialLayer> getLayers() {
		return this.layers;
	}
	 
	public void setupConfig(CommonSettings settings) {
		for(ICelestialLayer layer : layers)
			if(layer.existOnServer())
				settings.putSubConfig(layer.getLayerName(), layer.getConfigType());
	}
	
	public void initializeClient() throws IOException {
		for(ICelestialLayer layer : layers)
			if(!layer.existOnServer())
				layer.initialize(true, null);
	}
	
	public void initializeCommon(CommonSettings settings) throws IOException {
		for(ICelestialLayer layer : layers)
			if(layer.existOnServer())
				layer.initialize(this.isRemote, settings.getSubConfig(layer.getLayerName()));
	}
	
	public void intializeClientSettings(ClientSettings settings) {
		for(ICelestialLayer layer : layers)
			for(CelestialObject object : layer.getObjectList())
				object.getRenderCache().initialize(settings);
	}
	
	public void update(double year) {
		for(ICelestialLayer layer : layers)
			layer.updateLayer(year);
	}
	
	public void updateClient(ClientSettings settings, IStellarViewpoint viewpoint) {
		for(ICelestialLayer layer : layers)
			for(CelestialObject object : layer.getObjectList())
				object.getRenderCache().updateCache(settings, object, viewpoint);
	}

}
