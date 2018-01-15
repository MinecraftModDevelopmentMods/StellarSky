package stellarium.api;

import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stellarapi.api.render.IAdaptiveRenderer;
import stellarapi.api.world.worldset.WorldSet;

/**
 * Sky renderer type, there is overworld and end type by default. <p>
 * This type can be chosen on Configuration for each world.
 * */
public interface ISkyRenderType {

	/**
	 * Name for this type. Will be used on configuration.
	 * */
	public String getName();
	
	/**
	 * Whether or not this type accepts certain world
	 * @param worldSet the worldSet the worlds are in.
	 * @return <code>false</code> if this type is incompatible with the world, <code>true</code> otherwise
	 * */
	public boolean acceptFor(WorldSet worldSet);
	
	/**
	 * Creates new SkyRenderer.
	 * @param renderer the celestial renderer which renders.
	 * */
	@SideOnly(Side.CLIENT)
	public IAdaptiveRenderer createSkyRenderer(IRenderHandler celestialRenderer);

}
