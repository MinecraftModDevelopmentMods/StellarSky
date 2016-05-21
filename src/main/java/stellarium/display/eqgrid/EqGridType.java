package stellarium.display.eqgrid;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stellarium.display.IDisplayElementType;
import stellarium.display.IDisplayRenderer;

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
