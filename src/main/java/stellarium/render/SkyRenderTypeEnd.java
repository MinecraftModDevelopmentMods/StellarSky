package stellarium.render;

import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stellarapi.api.world.worldset.WorldSet;
import stellarium.api.IAdaptiveRenderer;
import stellarium.api.ISkyRenderType;

public class SkyRenderTypeEnd implements ISkyRenderType {

	@Override
	public String getName() {
		return "End Sky";
	}

	@Override
	public boolean acceptFor(WorldSet worldSet) {
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IAdaptiveRenderer createSkyRenderer(IRenderHandler subRenderer) {
		return new SkyRendererEnd(subRenderer);
	}

}
