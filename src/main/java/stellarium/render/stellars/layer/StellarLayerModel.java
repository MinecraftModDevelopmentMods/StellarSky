package stellarium.render.stellars.layer;

import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

import net.minecraft.server.MinecraftServer;
import stellarium.lib.render.IRenderModel;
import stellarium.stellars.layer.IStellarLayerType;
import stellarium.stellars.layer.StellarCollection;
import stellarium.stellars.layer.StellarObject;
import stellarium.stellars.layer.StellarObjectContainer;
import stellarium.stellars.layer.query.ILayerTempManager;
import stellarium.stellars.layer.query.MetadataQueryCache;
import stellarium.stellars.layer.query.QueryStellarObject;
import stellarium.stellars.layer.update.IMetadataUpdater;
import stellarium.stellars.layer.update.IUpdateTracked;
import stellarium.stellars.layer.update.MetadataUpdateTracker;

public class StellarLayerModel<Obj extends StellarObject> implements IRenderModel<LayerSettings, LayerUpdateInfo> {
	private StellarObjectContainer container;
	private StellarCollection<Obj> collection;

	private MetadataQueryCache<Obj, IUpdateTracked<IObjRenderCache<Obj>>> cache;
	private Map<Obj, IObjRenderCache<Obj>> cacheMap = Maps.newHashMap();
	private MetadataUpdateTracker<Obj, IObjRenderCache<Obj>> updateTracker;

	private RenderCacheLoader cacheLoader;
	private RenderCacheUpdater cacheUpdater;

	private ImmutableList<IObjRenderCache<Obj>> fromCache;

	public StellarLayerModel(StellarObjectContainer container) {
		this.container = container;
		this.updateTracker = new MetadataUpdateTracker(this.cacheUpdater = new RenderCacheUpdater());
	}
	
	public IStellarLayerType getLayerType() {
		return container.getType();
	}
	
	public String getConfigName() {
		return container.getConfigName();
	}

	@Override
	public void initialize(LayerSettings settings) {
		for(Map.Entry<Obj, IObjRenderCache<Obj>> entry : cacheMap.entrySet())
			entry.getValue().initialize(settings.getSettingsFor(entry.getKey()));
	}

	public void addRenderCache(Obj object, IObjRenderCache<Obj> renderCache) {
		cacheMap.put(object, renderCache);
	}
	
	public void removeCache(Obj object) {
		cacheMap.remove(object);
	}

	public void setupForWorld(StellarCollection<Obj> collection) {
		this.collection = collection;

		ILayerTempManager<Obj> manager = container.getType().getTempLoadManager();
		this.cache = manager == null? null : new MetadataQueryCache(
				this.cacheLoader = new RenderCacheLoader(manager), manager);
	}

	@Override
	public void update(LayerSettings settings, LayerUpdateInfo update) {
		cacheLoader.set(settings);
		cacheUpdater.set(settings,update);

		updateTracker.updateMap(this.cacheMap);

		if(this.cache != null)
			this.fromCache = ImmutableList.copyOf(updateTracker.addUpdateOnIteration(
					cache.query(new QueryStellarObject(update.currentDirection, update.currentRadius))));
	}
	
	public Iterable<IObjRenderCache<Obj>> getRenderCaches() {
		return Iterables.concat(cacheMap.values(), this.fromCache);
	}
	
	private class RenderCacheLoader implements Function<Obj, IUpdateTracked<IObjRenderCache<Obj>>> {
		private ILayerTempManager<Obj> manager;
		private LayerSettings settings;
		
		public RenderCacheLoader(ILayerTempManager<Obj> manager) {
			this.manager = manager;
		}

		public void set(LayerSettings settings) {
			this.settings = settings;
		}

		@Override
		public IUpdateTracked<IObjRenderCache<Obj>> apply(Obj object) {
			IObjRenderCache<Obj> cache = manager.loadCache(object);
			cache.initialize(settings.getSettingsFor(object));
			return updateTracker.createTracker(object, cache);
		}
	}

	private class RenderCacheUpdater implements IMetadataUpdater<Obj, IObjRenderCache<Obj>> {
		private LayerSettings settings;
		private LayerUpdateInfo update;
		
		@Override
		public long getCurrentTime() {
			return MinecraftServer.getServer().getEntityWorld().getTotalWorldTime();
		}

		public void set(LayerSettings settings, LayerUpdateInfo update) {
			this.settings = settings;
			this.update = update;
		}

		@Override
		public void update(Obj object, IObjRenderCache<Obj> metadata) {
			metadata.update(settings.getSettingsFor(object), update.getInfoFor(collection.loadImageFor(object)));
		}
	}

	public StellarLayerModel copy(StellarObjectContainer copied) {
		StellarLayerModel model = new StellarLayerModel(copied);
		model.cacheMap = Maps.newHashMap(this.cacheMap);
		return model;
	}
}
