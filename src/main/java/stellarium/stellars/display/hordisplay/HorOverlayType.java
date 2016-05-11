package stellarium.stellars.display.hordisplay;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import stellarium.stellars.display.IDisplayElementType;
import stellarium.stellars.display.IDisplayRenderer;

public class HorOverlayType implements IDisplayElementType<HorOverlaySettings, HorOverlayCache> {

	@Override
	public HorOverlaySettings generateSettings() {
		return new HorOverlaySettings();
	}

	@Override
	public HorOverlayCache generateCache() {
		return new HorOverlayCache();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IDisplayRenderer getRenderer() {
		return new HorOverlayRenderer();
	}

	@Override
	public String getName() {
		return "Horizontal_Coordinate_Grid";
	}

}
