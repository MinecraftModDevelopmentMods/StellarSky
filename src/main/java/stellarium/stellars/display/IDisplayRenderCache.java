package stellarium.stellars.display;

import stellarium.stellars.layer.IRenderCache;

public interface IDisplayRenderCache<Obj extends DisplayElement> extends IRenderCache<Obj, DisplaySettings> {
	public void setRenderId(int id);
}