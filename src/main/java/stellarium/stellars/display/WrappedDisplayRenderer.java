package stellarium.stellars.display;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import stellarium.render.ICelestialObjectRenderer;
import stellarium.render.StellarRenderInfo;

@SideOnly(Side.CLIENT)
public class WrappedDisplayRenderer implements ICelestialObjectRenderer<WrappedDisplayRenderCache> {

	private IDisplayRenderer internal;
	
	public WrappedDisplayRenderer(LayerDisplay.DisplayDelegate delegate) {
		this.internal = delegate.type.getRenderer();
	}

	@Override
	public void render(StellarRenderInfo info, WrappedDisplayRenderCache cache) {
		internal.render(info, cache.internal);
	}

}
