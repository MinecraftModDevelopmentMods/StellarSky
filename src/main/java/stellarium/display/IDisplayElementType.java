package stellarium.display;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface IDisplayElementType<Cfg extends PerDisplaySettings, Cache extends IDisplayCache<Cfg>> {
	public Cfg generateSettings();
	public Cache generateCache();
	
	@SideOnly(Side.CLIENT)
	public IDisplayRenderer<Cache> getRenderer();
	
	/**
	 * Configuration Name
	 * */
	public String getName();
}