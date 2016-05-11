package stellarium.stellars.display.ecdisplay;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import stellarium.stellars.display.IDisplayElementType;
import stellarium.stellars.display.IDisplayRenderer;

public class EcOverlayType implements IDisplayElementType<EcOverlaySettings, EcOverlayCache> {

	@Override
	public EcOverlaySettings generateSettings() {
		return new EcOverlaySettings();
	}

	@Override
	public EcOverlayCache generateCache() {
		return new EcOverlayCache();
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public IDisplayRenderer getRenderer() {
		return new EcOverlayRenderer();
	}

	@Override
	public String getName() {
		return "Ecliptic_Coordinate_Grid";
	}

}
