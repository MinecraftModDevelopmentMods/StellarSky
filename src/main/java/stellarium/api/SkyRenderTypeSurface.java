package stellarium.api;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.client.IRenderHandler;

public class SkyRenderTypeSurface implements ISkyRenderType {

	@Override
	public String getName() {
		return "Overworld Sky";
	}

	@Override
	public boolean acceptFor(String worldName) {
		return !worldName.equals("The End");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IRenderHandler createSkyRenderer(IRenderHandler subRenderer) {
		return subRenderer;
	}

}
