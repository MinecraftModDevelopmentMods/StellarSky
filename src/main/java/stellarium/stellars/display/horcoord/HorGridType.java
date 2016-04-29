package stellarium.stellars.display.horcoord;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import stellarium.stellars.display.PerDisplaySettings;
import stellarium.stellars.display.IDisplayElementType;
import stellarium.stellars.display.IDisplayRenderCache;
import stellarium.stellars.display.IDisplayRenderer;

public class HorGridType implements IDisplayElementType<HorGridSettings, HorGridCache> {

	@Override
	public HorGridSettings generateSettings() {
		return new HorGridSettings();
	}

	@Override
	public HorGridCache generateCache() {
		return new HorGridCache();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IDisplayRenderer getRenderer() {
		return new HorGridRenderer();
	}

	@Override
	public String getName() {
		return "Horizontal_Coordinate_Grid";
	}

}
