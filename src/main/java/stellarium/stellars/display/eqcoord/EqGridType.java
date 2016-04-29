package stellarium.stellars.display.eqcoord;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import stellarium.stellars.display.PerDisplaySettings;
import stellarium.stellars.display.IDisplayElementType;
import stellarium.stellars.display.IDisplayRenderCache;
import stellarium.stellars.display.IDisplayRenderer;

public class EqGridType implements IDisplayElementType<EqGridSettings, EqGridCache> {

	@Override
	public EqGridSettings generateSettings() {
		return new EqGridSettings();
	}

	@Override
	public EqGridCache generateCache() {
		return new EqGridCache();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IDisplayRenderer getRenderer() {
		return new EqGridRenderer();
	}

	@Override
	public String getName() {
		return "Equatorial_Coordinate_Grid";
	}

}
