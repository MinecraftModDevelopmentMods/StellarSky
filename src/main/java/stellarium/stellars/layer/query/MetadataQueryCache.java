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
import com.google.common.collect.Iterables;

import stellarium.stellars.layer.StellarObject;

public class MetadataQueryCache<Obj extends StellarObject, T> implements IMetadataQueryable<Obj, T> {

	private LoadingCache<QueryStellarObject, Collection<Obj>> objLoader;

	private Function<Obj, T> metadataLoader;
	private Cache<String, T> additionalCache;

	/**
	 * @param metadataLoader the metadata loader for specific object,
	 * 		only called on loading process caused by query/loader
	 * @param objLoader the object loader
	 * */
	public MetadataQueryCache(Function<Obj, T> metadataLoader, LoadingCache<QueryStellarObject, Collection<Obj>> objLoader) {
		this.metadataLoader = metadataLoader;
		this.objLoader = objLoader;

		this.additionalCache = CacheBuilder.newBuilder()
				.softValues().expireAfterAccess(1, TimeUnit.SECONDS).build();
	}

	@Override
	public Iterable<T> query(QueryStellarObject query) {
		return this.query(query, new LazyCacheLoader());
	}
	
	@Override
	public Function<Obj, T> lazyLoader() {
		return new LazyCacheLoader();
	}

	@Override
	public <S> Iterable<S> query(QueryStellarObject query, Function<Obj, S> lazyLoader) {
		Collection<Obj> queried = objLoader.getUnchecked(query);
		if(queried == null)
			return null;
		
		return Iterables.transform(queried, lazyLoader);
	}

	private class LazyCacheLoader implements Function<Obj, T> {
		private CachedAdditionalLoader loader = new CachedAdditionalLoader();
		
		@Override
		public T apply(Obj inspected) {
			try {
				loader.setObject(inspected);
				return additionalCache.get(inspected.getID(), this.loader);
			} catch (ExecutionException e) {
				// Should not be called.
				Throwables.propagate(e);
				return null;
			}
		}
	}

	private class CachedAdditionalLoader implements Callable<T> {
		private Obj object;

		@Override
		public T call() throws Exception {
			return metadataLoader.apply(this.object);
		}

		public void setObject(Obj inspected) {
			this.object = inspected;
		}
	}
}
