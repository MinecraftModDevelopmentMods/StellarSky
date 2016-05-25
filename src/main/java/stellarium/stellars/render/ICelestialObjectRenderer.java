package stellarium.stellars.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import stellarium.render.atmosphere.IAtmosphericTessellator;
import stellarium.stellars.layer.IRenderCache;

@SideOnly(Side.CLIENT)
public interface ICelestialObjectRenderer<Cache extends IRenderCache> {
	
	public void render(IAtmosphericTessellator tessellator, Cache cache);

}
