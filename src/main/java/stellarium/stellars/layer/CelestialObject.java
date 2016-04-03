package stellarium.stellars.layer;

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
