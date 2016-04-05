package stellarium.stellars.layer;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stellarium.render.StellarRenderInfo;

@SideOnly(Side.CLIENT)
public interface ICelestialLayerRenderer {
	
	public void preRender(StellarRenderInfo info);
	public void postRender(StellarRenderInfo info);

}
