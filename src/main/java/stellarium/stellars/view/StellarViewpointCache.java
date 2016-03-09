package stellarium.stellars.view;

import java.util.Map;

import com.google.common.collect.Maps;

import stellarium.stellars.base.IStellarObj;
import stellarium.util.math.SpCoord;

public class StellarViewpointCache {
	
	private Map<IStellarObj, CachedInfo> cacheMap = Maps.newHashMap();

	public static class CachedInfo {
		private SpCoord pos;
		private double mag;
	}
	
}
