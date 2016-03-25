package stellarium.stellars.sketch;

import java.util.HashMap;

import com.google.common.collect.Maps;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class CelestialObject {
	
	/** Client-only cache. */
	private IRenderCache cache;
	
	public CelestialObject(boolean isRemote) {
		if(isRemote)
			this.cache = this.generateCache();
	}
	
	public IRenderCache getRenderCache() {
		return this.cache;
	}
		
	public abstract IRenderCache generateCache();
	public abstract int getRenderId();

}
