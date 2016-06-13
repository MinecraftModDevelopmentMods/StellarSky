package stellarium.display;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IDisplayRenderer<Cache extends IDisplayCache> {

	public void render(DisplayRenderInfo info, Cache cache);

}
