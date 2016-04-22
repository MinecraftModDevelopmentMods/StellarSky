package stellarium.stellars.display;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import stellarapi.api.lib.config.IConfigHandler;
import stellarium.render.ICelestialObjectRenderer;
import stellarium.stellars.layer.IRenderCache;
import stellarium.stellars.layer.StellarObject;

public abstract class DisplayElement extends StellarObject implements IConfigHandler {

	private int renderId;
	private IDisplayRenderCache currentCache;
	
	public IDisplayRenderCache getCache() {
		if(this.currentCache == null)
			this.currentCache = this.generateCache();
		return this.currentCache;
	}
	
	protected abstract IDisplayRenderCache generateCache();

	@SideOnly(Side.CLIENT)
	public abstract ICelestialObjectRenderer getRenderer();

	public void setRenderId(int id) {
		currentCache.setRenderId(id);
		this.renderId = id;
	}	
}
