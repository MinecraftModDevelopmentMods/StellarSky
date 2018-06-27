package stellarium.stellars.render;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stellarium.render.stellars.access.EnumStellarPass;
import stellarium.render.stellars.layer.LayerRHelper;

@SideOnly(Side.CLIENT)
public interface ICelestialLayerRenderer {
	
	public void preRender(EnumStellarPass pass, LayerRHelper info);
	public void postRender(EnumStellarPass pass, LayerRHelper info);
	
	public boolean acceptPass(EnumStellarPass pass);

}
