package stellarium.api;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;

/**
 * Interface provided by Stellar Sky. <p>
 * Renders celestial sphere for certain background light and weather effect.
 * */
public interface ICelestialRenderer {
	/**
	 * Renders celestial sphere.
	 * @param mc the minecraft instance
	 * @param theWorld the world to render the celestial sphere
	 * @param skycolor background color of sky
	 * @param partialTicks the partial tick on this render tick
	 * */
	public void renderCelestial(Minecraft mc, WorldClient theWorld, float[] skycolor, float partialTicks);
	
	/**
	 * Renders sunrise/sunset effects.
	 * @param mc the minecraft instance
	 * @param theWorld the world to render the sunrise sunset effect
	 * @param colors the array of sunrise sunset colors which represents RGBA,
	 *  and can be gotten by {@link net.minecraft.world.WorldProvider#calcSunriseSunsetColors(float, float)
	 *  WorldProvider#calcSunriseSunsetColors}
	 * @param partialTicks the partial tick on this render tick
	 * */
	public void renderSunriseSunsetEffect(Minecraft mc, WorldClient theWorld, float[] colors, float partialTicks);

	/**
	 * Renders sky landscape. (This can be faraway ground landscape or some sky overlay)
	 * @param mc the minecraft instance
	 * @param theWorld the world to render the sky landscape
	 * @param partialTicks the partial tick on this render tick
	 * */
	public void renderSkyLandscape(Minecraft mc, WorldClient theWorld, float partialTicks);
}
