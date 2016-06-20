package stellarium.display;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IDisplayElementType<Cfg extends PerDisplaySettings, Cache extends IDisplayCache<Cfg>> {
	public Cfg generateSettings();
	public Cache generateCache();
	
	@SideOnly(Side.CLIENT)
	public IDisplayRenderer<Cache> getRenderer();
	
	/**
	 * Configuration Name, can be used as identifier
	 * */
	public String getName();
}