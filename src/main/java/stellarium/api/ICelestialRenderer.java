package stellarium.api;

import net.minecraft.client.Minecraft;

/**
 * Interface provided by Stellar Sky. <p>
 * Renders celestial sphere for certain background light and weather effect.
 * */
public interface ICelestialRenderer {
	/**
	 * Renders celestial sphere.
	 * @param mc the minecraft instance
	 * @param skycolor background color of sky
	 * @param weathereff weather effect; 1-rainstrength on default implementation
	 * @param partialTicks the partial tick on this render tick
	 * */
	public void renderCelestial(Minecraft mc, float[] skycolor, float weathereff, float partialTicks);
	
	/**
	 * Renders sunrise/sunset effects.
	 * @param mc the minecraft instance
	 * @param colors the array of sunrise sunset colors which represents RGBA,
	 *  and can be gotten by {@link net.minecraft.}
	 * @param partialTicks the partial tick on this render tick
	 * */
	public void renderSunriseSunsetEffect(Minecraft mc, float[] colors, float partialTicks);
}
