package stellarium.stellars.layer.query;

import java.util.Collection;

import com.google.common.cache.LoadingCache;

import stellarapi.api.ISkyEffect;
import stellarapi.api.lib.math.Matrix3;
import stellarium.render.stellars.layer.IObjRenderCache;
import stellarium.stellars.layer.IPerWorldImage;
import stellarium.stellars.layer.StellarObject;

/**
 * Cache for objecs which always gives objects unless the query was invalid.
 * */
public interface ILayerTempManager<Obj extends StellarObject> extends LoadingCache<QueryStellarObject, Collection<Obj>> {

	/**
	 * Creates image for the object.
	 * */
	public IPerWorldImage loadImage(Obj object);
	
	/**
	 * Creates render cache for the object.
	 * */
	public IObjRenderCache<Obj, ?, ?> loadCache(Obj object);
	
	/**
	 * Transforms query for ground to query for absolute positions.
	 * */
	public QueryStellarObject transformToAbsolute(
			QueryStellarObject queryGround, Matrix3 projToGround, ISkyEffect sky);

}
