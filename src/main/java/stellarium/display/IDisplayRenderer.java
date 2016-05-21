package stellarium.display;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IDisplayRenderer<Cache extends IDisplayCache> {

	public void render(DisplayRenderInfo info, Cache cache);

}
