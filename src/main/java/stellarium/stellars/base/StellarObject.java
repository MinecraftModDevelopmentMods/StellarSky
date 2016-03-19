package stellarium.stellars.base;

import java.util.HashMap;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

import stellarium.stellars.view.IStellarViewpoint;

public abstract class StellarObject {
	
	private HashMap<Integer, IPerDimensionCache> map = Maps.newHashMap();
	
	public void addCache(int dimensionId) {
		map.put(dimensionId, createCache());
	}
	
	public void removeCache(int dimensionId) {
		map.remove(dimensionId);
	}
	
	public void updateOnDimension(IStellarViewpoint viewpoint, int dimensionId) {
		map.get(dimensionId).update(viewpoint);
	}
	
	public IPerDimensionCache getCache(int dimensionId) {
		return map.get(dimensionId);
	}
	
	public abstract void update(double year);
	
	public abstract IPerDimensionCache createCache();
	
}
