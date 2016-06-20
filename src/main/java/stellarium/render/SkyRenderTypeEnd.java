package stellarium.render;

import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stellarium.api.ISkyRenderType;

public class SkyRenderTypeEnd implements ISkyRenderType {

	@Override
	public String getName() {
		return "End Sky";
	}

	@Override
	public boolean acceptFor(String worldName) {
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IRenderHandler createSkyRenderer(IRenderHandler subRenderer) {
		return new SkyRendererEnd(subRenderer);
	}

}
