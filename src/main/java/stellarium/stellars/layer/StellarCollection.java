package stellarium.stellars.layer;

import java.util.Collections;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import stellarapi.api.celestials.CelestialCollection;
import stellarapi.api.celestials.CelestialObject;
import stellarapi.api.lib.config.IConfigHandler;
import stellarapi.api.lib.config.INBTConfig;
import stellarapi.api.observe.SearchRegion;
import stellarium.render.stellars.layer.IObjRenderCache;
import stellarium.render.stellars.layer.StellarLayerModel;

public class StellarCollection<S extends StellarObject> extends CelestialCollection {
	private final StellarLayer<S, IConfigHandler, INBTConfig> type;
	private final String configName;

	private StellarLayerModel<S> layerModel;

	private SetMultimap<String, S> loadedObjects = HashMultimap.create();

	public StellarCollection(StellarLayer<S, IConfigHandler, INBTConfig> type, String configName) {
		super(type.name, type.type, type.searchOrder);
		this.type = type;
		this.configName = configName;
	}

	public void bindRenderModel(StellarLayerModel<S> layerModel) {
		this.layerModel = layerModel;
	}
	
	public StellarLayer<S, IConfigHandler, INBTConfig> getType() {
		return this.type;
	}
	
	public String getConfigName() {
		return this.configName;
	}

	/**
	 * Finds all visible celestial objects in certain region.
	 * 
	 * @param region the search region in absolute coordinates
	 * @param multPower multiplying power of the viewer
	 * @param efficiency quantum efficiency of the viewer
	 * @return all objects in the search range which is visible
	 */
	@Override
	public Set<CelestialObject> findIn(SearchRegion region, float efficiency, float multPower) {
		// TODO AA Fill in this
		return Collections.emptySet();
	}

	public Set<S> getLoadedObjects(String identifier) {
		return loadedObjects.get(identifier);
	}

	public S getLoadedSingleton(String identifier) {
		Set<S> set = loadedObjects.get(identifier);
		if(set.size() != 1)
			throw new IllegalArgumentException(
					String.format("Loaded objects for %s is not singleton!", identifier));
		
		return set.iterator().next();
	}


	public void loadObject(String identifier, S object) {
		loadedObjects.put(identifier, object);
	}

	public void addRenderCache(S object, IObjRenderCache<? extends S,?> cache) {
		if(this.layerModel != null)
			layerModel.addRenderCache(object, cache);
	}

	public StellarCollection<S> copyFromClient() {
		StellarCollection<S> copied = new StellarCollection<>(this.type, this.configName);
		copied.loadedObjects = HashMultimap.create(copied.loadedObjects);
		
		return copied;
	}
}
