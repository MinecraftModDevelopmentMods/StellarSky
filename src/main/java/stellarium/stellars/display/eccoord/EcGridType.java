package stellarium.stellars.display.eccoord;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import stellarium.render.ICelestialObjectRenderer;
import stellarium.stellars.display.PerDisplaySettings;
import stellarium.stellars.display.IDisplayElementType;
import stellarium.stellars.display.IDisplayRenderCache;
import stellarium.stellars.display.IDisplayRenderer;

public class EcGridType implements IDisplayElementType<EcGridSettings, EcGridCache> {

	@Override
	public EcGridSettings generateSettings() {
		return new EcGridSettings();
	}

	@Override
	public EcGridCache generateCache() {
		return new EcGridCache();
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public IDisplayRenderer getRenderer() {
		return new EcGridRenderer();
	}

	@Override
	public String getName() {
		return "Ecliptic_Coordinate_Grid";
	}

}
