package stellarium.stellars.display.eccoord;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import stellarium.render.ICelestialObjectRenderer;
import stellarium.stellars.display.DisplayElementSettings;
import stellarium.stellars.display.IDisplayElementType;
import stellarium.stellars.display.IDisplayRenderCache;
import stellarium.stellars.display.IDisplayRenderer;

public class DisplayEcCoordType implements IDisplayElementType {

	@Override
	public DisplayElementSettings generateSettings() {
		return new DisplayEcCoordSettings();
	}

	@Override
	public IDisplayRenderCache generateCache() {
		return new DisplayEcCoordCache();
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public IDisplayRenderer getRenderer() {
		return new DisplayEcCoordRenderer();
	}

	@Override
	public String getName() {
		return "Ecliptic_Coordinate_Grid";
	}

}
