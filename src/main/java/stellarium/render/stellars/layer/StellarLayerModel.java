package stellarium.render.stellars.layer;

import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

import net.minecraft.server.MinecraftServer;
import stellarapi.api.lib.config.IConfigHandler;
import stellarium.client.ClientSettings;
import stellarium.lib.hierarchy.Hierarchy;
import stellarium.render.stellars.access.IStellarChecker;
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
import stellarium.view.ViewerInfo;

@Hierarchy
public class StellarLayerModel<Obj extends StellarObject> {
	private StellarObjectContainer container;
	private StellarCollection<Obj> collection;

	private MetadataQueryCache<Obj, IUpdateTracked<IObjRenderCache>> cache;
	private Map<Obj, IObjRenderCache> cacheMap = Maps.newHashMap();
	private MetadataUpdateTracker<Obj, IObjRenderCache> updateTracker;

	private RenderCacheLoader cacheLoader;
	private RenderCacheUpdater cacheUpdater;

	private ImmutableList<IObjRenderCache> fromCache;

	public StellarLayerModel(StellarObjectContainer container) {
		this.container = container;
		this.updateTracker = new MetadataUpdateTracker(this.cacheUpdater = new RenderCacheUpdater());
	}

	public IStellarLayerType getLayerType() {
		return container.getType();
	}

	public void addRenderCache(Obj object, IObjRenderCache<Obj, ?, ?> renderCache) {
		cacheMap.put(object, renderCache);
	}
	
	public void removeCache(Obj object) {
		cacheMap.remove(object);
	}


	public void onLoadCollection(StellarCollection<Obj> collection) {
		this.collection = collection;

		ILayerTempManager<Obj> manager = container.getType().getTempLoadManager();
		this.cache = manager == null? null : new MetadataQueryCache(
				this.cacheLoader = new RenderCacheLoader(manager), manager);
	}
	
	public void updateSettings(ClientSettings settings) {
		cacheLoader.set(settings);
		for(Map.Entry<Obj, IObjRenderCache> entry : cacheMap.entrySet())
			entry.getValue().updateSettings(settings, this.getSubSettings(settings), entry.getKey());
	}

	public void onStellarTick(ViewerInfo update, IStellarChecker checker) {
		cacheUpdater.set(update, checker);

		updateTracker.updateMap(this.cacheMap);

		if(this.cache != null)
			this.fromCache = ImmutableList.copyOf(updateTracker.addUpdateOnIteration(
					cache.query(new QueryStellarObject(update.currentDirection, update.currentFOVRadius))));
	}

	public Iterable<IObjRenderCache> getRenderCaches() {
		return Iterables.concat(cacheMap.values(), this.fromCache);
	}
	
	private IConfigHandler getSubSettings(ClientSettings settings) {
		return settings.getSubConfig(container.getConfigName());
	}
	
	private class RenderCacheLoader implements Function<Obj, IUpdateTracked<IObjRenderCache>> {
		private ILayerTempManager<Obj> manager;
		private ClientSettings settings;
		
		public RenderCacheLoader(ILayerTempManager<Obj> manager) {
			this.manager = manager;
		}

		public void set(ClientSettings settings) {
			this.settings = settings;
		}

		@Override
		public IUpdateTracked<IObjRenderCache> apply(Obj object) {
			IObjRenderCache cache = manager.loadCache(object);
			cache.updateSettings(this.settings, getSubSettings(this.settings), object);
			return updateTracker.createTracker(object, cache);
		}
	}

	private class RenderCacheUpdater implements IMetadataUpdater<Obj, IObjRenderCache> {
		private ViewerInfo update;
		private IStellarChecker checker;
		
		@Override
		public long getCurrentTime() {
			return MinecraftServer.getServer().getEntityWorld().getTotalWorldTime();
		}

		public void set(ViewerInfo update, IStellarChecker checker) {
			this.update = update;
			this.checker = checker;
		}

		@Override
		public void update(Obj object, IObjRenderCache metadata) {
			metadata.updateCache(object, collection.loadImageFor(object), this.update, this.checker);
		}
	}

	public StellarLayerModel copy(StellarObjectContainer copied) {
		StellarLayerModel model = new StellarLayerModel(copied);
		model.cacheMap = Maps.newHashMap(this.cacheMap);
		copied.bindRenderModel(this);
		return model;
	}
}
