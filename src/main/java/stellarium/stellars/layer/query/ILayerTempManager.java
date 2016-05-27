package stellarium.stellars.layer.query;

import java.util.Collection;

import com.google.common.cache.LoadingCache;

import stellarium.stellars.layer.IPerWorldImage;
import stellarium.stellars.layer.StellarObject;

/**
 * Cache for objecs which always gives objects unless the query was invalid.
 * */
public interface ILayerTempManager<Obj extends StellarObject> extends LoadingCache<QueryStellarObject, Collection<Obj>> {

	public IPerWorldImage loadImage(Obj object);
	
	/**
	 * Loads objects temporally.
	 * Can provide <code>null</code> as value when it is matched with valid key
	 * @param query query to load objects
	 * @param mapToFill the map to fill for the query
	 * @param cache previous results to queries.
	 * */
	/*public <T> Map<Obj, T> temporalLoadObjectsInRange(
			QueryStellarObject query, Map<Obj, T> mapToFill,
			Cache<QueryStellarObject, Map<Obj, T>> cache);*/
	
}
