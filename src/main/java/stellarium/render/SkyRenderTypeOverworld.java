package stellarium.render;

import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stellarium.api.ICelestialRenderer;
import stellarium.api.ISkyRendererType;

public class SkyRenderTypeOverworld implements ISkyRendererType {

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
	public IRenderHandler createSkyRenderer(ICelestialRenderer subRenderer) {
		return new SkyRenderer(subRenderer);
	}

}
