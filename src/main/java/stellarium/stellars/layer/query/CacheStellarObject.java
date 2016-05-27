package stellarium.stellars.layer.query;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.Iterables;

import net.minecraft.server.MinecraftServer;
import stellarium.stellars.layer.StellarObject;

public class CacheStellarObject<Obj extends StellarObject, T> {

	private LoadingCache<QueryStellarObject, Collection<Obj>> objLoader;
	private IMetadataManager<Obj, T> metadataManager;
	private Cache<String, CachedAdditionals> additionalCache;
	private CachedAdditionalLoader loader = new CachedAdditionalLoader();
	private long currentTick;

	public CacheStellarObject(IMetadataManager<Obj, T> metadataManager,
			LoadingCache<QueryStellarObject, Collection<Obj>> objLoader) {
		this.objLoader = objLoader;
		this.metadataManager = metadataManager;

		this.additionalCache = CacheBuilder.newBuilder()
				.softValues()
				.expireAfterAccess(1, TimeUnit.SECONDS).build();
	}

	/**
	 * Gives the iterable for additional metadata loaded for the objects in query,
	 * Or return null if query is invalid.
	 * */	
	public Iterable<T> query(QueryStellarObject query) {
		this.currentTick = getCurrentTick();
		Collection<Obj> queried = objLoader.getUnchecked(query);
		if(queried == null)
			return null;
		
		return Iterables.transform(queried, new Function<Obj, T>() {
			@Override
			public T apply(Obj inspected) {
				CachedAdditionals metadata;
				try {
					loader.setObject(inspected);
					metadata = additionalCache.get(inspected.getID(), loader);
					if(metadata.updateTick != currentTick)
						metadataManager.updateMetadata(inspected, metadata.cached);
					return metadata.cached;
				} catch (ExecutionException e) {
					// Should not be called.
					Throwables.propagate(e);
					return null;
				}
			}
		});
	}

	private class CachedAdditionalLoader implements Callable<CachedAdditionals> {
		private Obj object;

		@Override
		public CachedAdditionals call() throws Exception {
			return new CachedAdditionals(metadataManager.loadMetadata(this.object), currentTick);
		}

		public void setObject(Obj inspected) {
			this.object = inspected;
		}
	}
	
	private class CachedAdditionals {		
		long updateTick;
		T cached;
		
		public CachedAdditionals(T cached, long currentTick) {
			this.cached = cached;
			this.updateTick = currentTick;
		}
	}
	
	private static long getCurrentTick() {
		return MinecraftServer.getServer().getEntityWorld().getTotalWorldTime();
	}
}
