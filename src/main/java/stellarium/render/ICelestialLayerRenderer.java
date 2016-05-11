package stellarium.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
