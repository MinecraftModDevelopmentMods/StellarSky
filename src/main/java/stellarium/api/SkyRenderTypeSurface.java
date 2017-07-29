package stellarium.api;

import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
	public IAdaptiveRenderer createSkyRenderer(IRenderHandler subRenderer) {
		return new SkyRendererSurface(subRenderer);
	}

}
