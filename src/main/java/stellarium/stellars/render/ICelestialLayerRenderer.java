package stellarium.stellars.render;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stellarium.render.stellars.access.EnumStellarPass;
import stellarium.render.stellars.access.IStellarTessellator;

@SideOnly(Side.CLIENT)
public interface ICelestialLayerRenderer {
	
	public void preRender(IStellarTessellator tessellator, EnumStellarPass pass);
	public void postRender(IStellarTessellator tessellator, EnumStellarPass pass);
	
	public boolean acceptPass(EnumStellarPass pass);

}
