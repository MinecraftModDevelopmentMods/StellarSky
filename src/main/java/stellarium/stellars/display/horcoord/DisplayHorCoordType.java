package stellarium.stellars.display.horcoord;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import stellarium.stellars.display.DisplayElementSettings;
import stellarium.stellars.display.IDisplayElementType;
import stellarium.stellars.display.IDisplayRenderCache;
import stellarium.stellars.display.IDisplayRenderer;

public class DisplayHorCoordType implements IDisplayElementType {

	@Override
	public DisplayElementSettings generateSettings() {
		return new DisplayHorCoordSettings();
	}

	@Override
	public IDisplayRenderCache generateCache() {
		return new DisplayHorCoordCache();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IDisplayRenderer getRenderer() {
		return new DisplayHorCoordRenderer();
	}

	@Override
	public String getName() {
		return "Horizontal_Coordinate_Grid";
	}

}
