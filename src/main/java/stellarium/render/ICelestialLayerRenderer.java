package stellarium.render;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface ICelestialLayerRenderer {
	
	public void preRender(StellarRenderInfo info);
	public void postRender(StellarRenderInfo info);
	
	/**
	 * Whether this layer will accept specific Stellar Render Pass or not.
	 * @see EnumRenderPass
	 * */
	public boolean acceptPass(EnumRenderPass pass);

}
