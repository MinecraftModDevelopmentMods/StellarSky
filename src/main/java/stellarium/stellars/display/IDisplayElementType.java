package stellarium.stellars.display;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import stellarium.render.ICelestialObjectRenderer;

public interface IDisplayElementType {
	public DisplayElementSettings generateSettings();
	public IDisplayRenderCache generateCache();
	
	@SideOnly(Side.CLIENT)
	public IDisplayRenderer getRenderer();
	
	public String getName();
}