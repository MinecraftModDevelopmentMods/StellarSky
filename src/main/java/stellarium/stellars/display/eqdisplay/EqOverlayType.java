package stellarium.stellars.display.eqdisplay;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import stellarium.stellars.display.IDisplayElementType;
import stellarium.stellars.display.IDisplayRenderer;

public class EqOverlayType implements IDisplayElementType<EqOverlaySettings, EqOverlayCache> {

	@Override
	public EqOverlaySettings generateSettings() {
		return new EqOverlaySettings();
	}

	@Override
	public EqOverlayCache generateCache() {
		return new EqOverlayCache();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IDisplayRenderer getRenderer() {
		return new EqOverlayRenderer();
	}

	@Override
	public String getName() {
		return "Equatorial_Coordinate_Grid";
	}

}
