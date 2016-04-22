package stellarium.stellars.display;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import stellarapi.api.lib.config.IConfigHandler;
import stellarium.render.ICelestialObjectRenderer;
import stellarium.stellars.layer.IRenderCache;
import stellarium.stellars.layer.StellarObject;

public abstract class DisplayElement extends StellarObject implements IConfigHandler {

	protected int renderId;
	
	public abstract IRenderCache generateCache();

	@SideOnly(Side.CLIENT)
	public abstract ICelestialObjectRenderer getRenderer();

	public abstract DisplayElement copy();

	public void setRenderID(int id) {
		this.renderId = id;
	}
	
	
}
