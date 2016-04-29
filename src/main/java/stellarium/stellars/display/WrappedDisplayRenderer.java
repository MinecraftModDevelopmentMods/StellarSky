package stellarium.stellars.display;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import stellarium.render.ICelestialObjectRenderer;
import stellarium.render.StellarRenderInfo;

@SideOnly(Side.CLIENT)
public class WrappedDisplayRenderer<Cfg extends PerDisplaySettings, Cache extends IDisplayRenderCache<Cfg>>
implements ICelestialObjectRenderer<WrappedDisplayRenderCache<Cfg, Cache>> {

	private IDisplayRenderer<Cache> internal;
	
	public WrappedDisplayRenderer(DisplayRegistry.Delegate<Cfg, Cache> delegate) {
		this.internal = delegate.getType().getRenderer();
	}

	@Override
	public void render(StellarRenderInfo info, WrappedDisplayRenderCache<Cfg, Cache> cache) {
		internal.render(info, cache.internal);
	}

}
