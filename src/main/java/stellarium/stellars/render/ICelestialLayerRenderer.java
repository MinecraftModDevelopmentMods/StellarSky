package stellarium.stellars.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import stellarium.render.atmosphere.IAtmosphericTessellator;

@SideOnly(Side.CLIENT)
public interface ICelestialLayerRenderer {
	
	public void preRender(IAtmosphericTessellator tessellator, boolean forOpaque, boolean hasTexture);
	public void postRender(IAtmosphericTessellator tessellator, boolean forOpaque, boolean hasTexture);
	
	public boolean acceptPass(boolean forOpaque, boolean hasTexture);

}
