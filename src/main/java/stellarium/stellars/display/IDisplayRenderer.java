package stellarium.stellars.display;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import stellarium.render.StellarRenderInfo;

@SideOnly(Side.CLIENT)
public interface IDisplayRenderer<Cache extends IDisplayRenderCache> {

	void render(StellarRenderInfo info, Cache internal);

}
