package stellarium.stellars.sketch;

import java.util.HashMap;

import com.google.common.collect.Maps;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class CelestialObject {
	
	/** Client-only cache. */
	private IRenderCache cache;
	private HashMap<Integer, IPerDimensionCache> map = Maps.newHashMap();
	
	public CelestialObject(boolean isRemote) {
		if(isRemote)
			this.cache = this.generateCache();
	}
	
	public IRenderCache getRenderCache() {
		return this.cache;
	}
	
	public IPerDimensionCache getDimensionCache(int dimId) {
		return map.get(dimId);
	}
	
	public abstract IRenderCache generateCache();
	public abstract int getRenderId();

}
