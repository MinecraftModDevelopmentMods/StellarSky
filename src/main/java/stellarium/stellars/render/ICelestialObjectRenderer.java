package stellarium.stellars.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import stellarium.render.stellars.access.IStellarTessellator;
import stellarium.stellars.layer.IRenderCache;

@SideOnly(Side.CLIENT)
public interface ICelestialObjectRenderer<Cache extends IRenderCache> {
	
	public void render(IStellarTessellator tessellator, Cache cache);

}
