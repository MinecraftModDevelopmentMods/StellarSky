package stellarium.api;

import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Sky renderer type, there is overworld and end type by default. <p>
 * This type can be chosen on Configuration for each world.
 * */
public interface ISkyRendererType {

	/**
	 * Name for this type. Will be used on configuration.
	 * */
	public String getName();
	
	/**
	 * Whether or not this type accepts certain world
	 * @param worldName the name of the world(dimension)
	 * @return <code>false</code> if this type is incompatible with the world, <code>true</code> otherwise
	 * */
	public boolean acceptFor(String worldName);
	
	/**
	 * Creates new SkyRenderer.
	 * @param renderer the celestial renderer
	 * */
	@SideOnly(Side.CLIENT)
	public IRenderHandler createSkyRenderer(ICelestialRenderer renderer);

}
