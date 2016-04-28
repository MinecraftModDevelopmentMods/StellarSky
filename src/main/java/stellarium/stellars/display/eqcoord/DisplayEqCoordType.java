package stellarium.stellars.display.eqcoord;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import stellarium.stellars.display.DisplayElementSettings;
import stellarium.stellars.display.IDisplayElementType;
import stellarium.stellars.display.IDisplayRenderCache;
import stellarium.stellars.display.IDisplayRenderer;

public class DisplayEqCoordType implements IDisplayElementType {

	@Override
	public DisplayElementSettings generateSettings() {
		return new DisplayEqCoordSettings();
	}

	@Override
	public IDisplayRenderCache generateCache() {
		return new DisplayEqCoordCache();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IDisplayRenderer getRenderer() {
		return new DisplayEqCoordRenderer();
	}

	@Override
	public String getName() {
		return "Equatorial_Coordinate_Grid";
	}

}
