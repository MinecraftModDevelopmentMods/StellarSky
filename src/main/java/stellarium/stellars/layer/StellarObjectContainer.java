package stellarium.stellars.layer;

import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;

import stellarapi.api.lib.config.IConfigHandler;
import stellarapi.api.lib.config.INBTConfig;
import stellarium.client.ClientSettings;

public class StellarObjectContainer<Obj extends StellarObject, ClientConfig extends IConfigHandler> {
	
	private boolean isRemote;
	private IStellarLayerType<Obj, ClientConfig, INBTConfig> type;
	private String configName;
	
	private SetMultimap<String, Obj> loadedObjects = HashMultimap.create();
	private Map<Obj, IRenderCache> renderCacheMap = Maps.newHashMap();
	private Map<Obj, IPerWorldImageType> imageTypeMap = Maps.newHashMap();
	private Set<Obj> addedSet = Sets.newHashSet();
	private Set<Obj> removedSet = Sets.newHashSet();
	private long previousUpdateTick = -1L;
	
	public StellarObjectContainer(boolean isRemote, IStellarLayerType<Obj, ClientConfig, INBTConfig> type, String configName) {
		this.isRemote = isRemote;
		this.type = type;
		this.configName = configName;
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
		loadedObjects.put(identifier, object);
	}
	
	public void addRenderCache(Obj object, IRenderCache renderCache) {
		if(this.isRemote)
			renderCacheMap.put(object, renderCache);
	}
	
	public void addImageType(Obj object, IPerWorldImageType imageType) {
		imageTypeMap.put(object, imageType);
		addedSet.add(object);
	}
	
	public void addImageType(Obj object, final Class<? extends IPerWorldImage> imageClass) {
		this.addImageType(object, new IPerWorldImageType() {
			@Override
			public IPerWorldImage generateImage() {
				try {
					return imageClass.newInstance();
				} catch (Exception exc) {
					Throwables.propagate(exc);
					return null;
				}
			}
		});
	}
	
	
	public void unloadObject(String identifier, Obj object) {
		loadedObjects.remove(identifier, object);

		if(this.isRemote && renderCacheMap.containsKey(object))
			renderCacheMap.remove(object);
		
		if(imageTypeMap.containsKey(object))
		{
			imageTypeMap.remove(object);
			removedSet.add(object);
		}
	}
	
	
	public void addCollection(StellarCollection image) {
		image.addImages(imageTypeMap.keySet(), this.imageTypeMap);
	}
	
	public void updateCollection(StellarCollection image, long currentUniversalTick) {
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
	
	
	private boolean initialized = false;
	
	public void reloadClientSettings(ClientSettings settings, ClientConfig specificSettings) {
		this.initialized = true;
		for(Map.Entry<Obj, IRenderCache> entry : renderCacheMap.entrySet())
			entry.getValue().initialize(settings, specificSettings, entry.getKey());
	}

	public void updateClient(ClientSettings settings, ClientConfig specificSettings, StellarCacheInfo info) {
		if(!this.initialized)
			return;
		
		for(Map.Entry<Obj, IRenderCache> entry : renderCacheMap.entrySet())
			entry.getValue().updateCache(settings, specificSettings, entry.getKey(), info);
	}

	public Iterable<IRenderCache> getRenderCacheList(Ordering<Obj> ordering) {
		if(ordering == null)
			return renderCacheMap.values();
		return Iterables.transform(ordering.immutableSortedCopy(renderCacheMap.keySet()),
				new Function<Obj, IRenderCache>() {
					@Override
					public IRenderCache apply(Obj input) {
						return renderCacheMap.get(input);
					}
		});
	}

}
