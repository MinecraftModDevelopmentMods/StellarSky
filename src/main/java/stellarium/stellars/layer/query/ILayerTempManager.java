package stellarium.stellars.layer.query;

import java.util.Collection;

import com.google.common.cache.LoadingCache;

import stellarium.render.stellars.layer.IObjRenderCache;
import stellarium.stellars.layer.IPerWorldImage;
import stellarium.stellars.layer.StellarObject;

/**
 * Cache for objecs which always gives objects unless the query was invalid.
 * */
public interface ILayerTempManager<Obj extends StellarObject> extends LoadingCache<QueryStellarObject, Collection<Obj>> {

	public IPerWorldImage loadImage(Obj object);
	public IObjRenderCache<Obj> loadCache(Obj object);

}
