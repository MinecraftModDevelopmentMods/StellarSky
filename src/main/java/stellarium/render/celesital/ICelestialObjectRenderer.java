package stellarium.render.celesital;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import stellarium.stellars.layer.IRenderCache;

@SideOnly(Side.CLIENT)
public interface ICelestialObjectRenderer<Cache extends IRenderCache> {
	
	public void render(StellarRenderInfo info, Cache cache);

}
