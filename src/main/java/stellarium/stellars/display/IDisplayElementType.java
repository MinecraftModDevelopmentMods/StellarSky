package stellarium.stellars.display;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface IDisplayElementType<Cfg extends PerDisplaySettings, Cache extends IDisplayRenderCache<Cfg>> {
	public Cfg generateSettings();
	public Cache generateCache();
	
	@SideOnly(Side.CLIENT)
	public IDisplayRenderer<Cache> getRenderer();
	
	public String getName();
}