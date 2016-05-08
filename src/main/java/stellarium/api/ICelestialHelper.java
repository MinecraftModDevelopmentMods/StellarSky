package stellarium.api;

import net.minecraft.util.MathHelper;
import stellarapi.api.lib.math.Spmath;
import stellarapi.api.optics.EnumRGBA;
import stellarapi.api.optics.Wavelength;

public interface ICelestialHelper {
	
	/**
	 * Calculates current celestial angle. <p>
	 * Basically for WorldProvider. <p>
	 * @param worldTime current world time
	 * @param partialTicks the partial tick
	 * */
	public float calculateCelestialAngle(long worldTime, float partialTicks);

	/**
	 * Gets current sun height rate.
	 * @param partialTicks the partial tick
	 * @return <code>sin(Height_Angle_Of_Sun)</code>
	 * */
	public float getSunHeightFactor(float partialTicks);

	/**
	 * Gets current sunlight factor.
	 * @param color the color to get sunlight factor
	 * @param partialTicks the partial tick
	 * */
	public float getSunlightFactor(EnumRGBA color, float partialTicks);
	
	/**
	 * Gets current sunlight factor which affects rendering of the sky.
	 * @param partialTicks the partial tick
	 * */
	public float getSunlightRenderBrightnessFactor(float partialTicks);

	/**
	 * Gets current sky transmission factor.
	 * @param partialTicks the partial tick
	 * */
	public float getSkyTransmissionFactor(float partialTicks);

	/**
	 * Gets current sunrise sunset factor(brightness of sunrise/sunset color).
	 * @param partialTicks the partial tick
	 * */
	public float calculateSunriseSunsetFactor(EnumRGBA color, float partialTicks);

	/**
	 * Gets dispersion factor. (brightness of sky itself, not ground light level) <p>
	 * @param partialTicks the partial tick
	 * */
	public float getDispersionFactor(EnumRGBA color, float partialTicks);

	/**
	 * Gets light pollution factor.
	 * @param partialTicks the partial tick
	 * */
	public float getLightPollutionFactor(EnumRGBA color, float partialTicks);

	/**
	 * Gets current moon phase time.
	 * @param worldTime the current World Time. If it isn't, this method will give undefined result.
	 * */
	public int getCurrentMoonPhase(long worldTime);
	
	/**
	 * Gets current moon phase factor. <p>
	 * */
	public float getCurrentMoonPhaseFactor();
	
	
	public float minimumSkyRenderBrightness();

}
