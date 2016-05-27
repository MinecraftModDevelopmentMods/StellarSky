package stellarium.stellars.layer;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;

import stellarapi.api.lib.config.IConfigHandler;
import stellarapi.api.lib.config.INBTConfig;
import stellarium.client.ClientSettings;
import stellarium.render.stellars.access.IAtmosphericChecker;
import stellarium.render.stellars.layer.IObjRenderCache;
import stellarium.render.stellars.layer.StellarLayerModel;
import stellarium.view.ViewerInfo;

public class StellarObjectContainer<Obj extends StellarObject, ClientConfig extends IConfigHandler> {

	private boolean isRemote;
	private IStellarLayerType<Obj, ClientConfig, INBTConfig> type;
	private String configName;
	
	private StellarLayerModel layerModel;

	private SetMultimap<String, Obj> loadedObjects = HashMultimap.create();
	private Map<Obj, Callable<IPerWorldImage>> imageTypeMap = Maps.newHashMap();
	private Set<Obj> addedSet = Sets.newHashSet();
	private Set<Obj> removedSet = Sets.newHashSet();
	private long previousUpdateTick = -1L;

	public StellarObjectContainer(boolean isRemote, IStellarLayerType<Obj, ClientConfig, INBTConfig> type, String configName) {
		this.isRemote = isRemote;
		this.type = type;
		this.configName = configName;
	}
	
	public void bindRenderModel(StellarLayerModel layerModel) {
		this.layerModel = layerModel;
	}
	
	public IStellarLayerType<Obj, ClientConfig, INBTConfig> getType() {
		return this.type;
	}
	
	public String getConfigName() {
		return this.configName;
	}

	public Set<Obj> getLoadedObjects(String identifier) {
		return loadedObjects.get(identifier);
	}

	public Obj getLoadedSingleton(String identifier) {
		Set<Obj> set = loadedObjects.get(identifier);
		if(set.size() != 1)
			throw new IllegalArgumentException(
					String.format("Loaded objects for %s is not singleton!", identifier));
		
		return set.iterator().next();
	}


	public void loadObject(String identifier, Obj object) {
		/*if(loadedObjects.containsEntry(identifier, object))
			loadedObjects.remove(identifier, object);*/
		loadedObjects.put(identifier, object);
	}

	public void addRenderCache(Obj object, IObjRenderCache<Obj> cache) {
		if(this.layerModel != null)
			layerModel.addRenderCache(object, cache);
	}

	public void addImageType(Obj object, Callable<IPerWorldImage> imageType) {
		imageTypeMap.put(object, imageType);
		addedSet.add(object);
	}

	public void addImageType(Obj object, final Class<? extends IPerWorldImage> imageClass) {
		this.addImageType(object, new Callable<IPerWorldImage>() {
			@Override
			public IPerWorldImage call() throws Exception {
				return imageClass.newInstance();
			}
		});
	}
	
	
	public void unloadObject(String identifier, Obj object) {
		loadedObjects.remove(identifier, object);

		if(this.layerModel != null)
			layerModel.removeCache(object);

		if(imageTypeMap.containsKey(object)) {
			imageTypeMap.remove(object);
			removedSet.add(object);
		}
	}
	
	
	public void addCollection(StellarCollection<Obj> image) {
		image.addImages(imageTypeMap.keySet(), this.imageTypeMap);
	}
	
	public void updateCollection(StellarCollection<Obj> image, long currentUniversalTick) {
		if(this.previousUpdateTick != currentUniversalTick) {
			addedSet.clear();
			removedSet.clear();
			this.previousUpdateTick = currentUniversalTick;
		}

		if(!addedSet.isEmpty())
			image.addImages(this.addedSet, this.imageTypeMap);
		if(!removedSet.isEmpty())
			image.removeImages(this.removedSet);

		image.update();
	}

	
	public StellarObjectContainer<Obj, ClientConfig> copy() {
		StellarObjectContainer copied = new StellarObjectContainer(this.isRemote, this.type, this.configName);
		copied.loadedObjects = HashMultimap.create(copied.loadedObjects);
		copied.imageTypeMap = Maps.newHashMap(this.imageTypeMap);
		copied.layerModel = layerModel.copy(copied);
		
		return copied;
	}
}
