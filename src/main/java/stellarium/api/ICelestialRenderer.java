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
	 * @param bglight background light; Sky R+G+B on default implementation
	 * @param weathereff weather effect; 1-rainstrength on default implementation
	 * @param partialTicks the partial tick on this render tick
	 * */
	public void renderCelestial(Minecraft mc, float bglight, float weathereff, float partialTicks);
}
