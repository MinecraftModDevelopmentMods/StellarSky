package stellarium.stellars.render;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stellarium.render.stellars.CRenderHelper;
import stellarium.render.stellars.access.EnumStellarPass;
import stellarium.render.stellars.layer.LayerRI;

@SideOnly(Side.CLIENT)
public interface ICelestialLayerRenderer {
	
	public void preRender(EnumStellarPass pass, LayerRI info);
	public void postRender(EnumStellarPass pass, LayerRI info);
	
	public boolean acceptPass(EnumStellarPass pass);

}
