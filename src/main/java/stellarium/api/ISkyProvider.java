package stellarium.api;

import org.lwjgl.util.vector.Vector3f;

import net.minecraft.util.ResourceLocation;

/**
 * Interface to provide sky information for certain world from Stellar Sky. <p>
 * Provided by Stellar Sky.
 * */
public interface ISkyProvider {
	
	/**
	 * Length of a day in a tick.
	 * */
	public double getDayLength();
	
	/**
	 * Length of lunar month (lunar cycle) in a day.
	 * */
	public double getLunarMonthLength();
	
	/**
	 * Length of a year in a day.
	 * */
	public double getYearLength();
	
	
	/**
	 * Current time offset during a day. <p>
	 * Should be in range [0.0, 1.0) <p>
	 * Normally,
	 * 0.0 is near dawn.
	 * 0.25 is midday.
	 * 0.5 is near dusk.
	 * 0.75 is midnight.
	 * */
	public double getDaytimeOffset();
	
	/**
	 * Current time offset during a day. <p>
	 * Should be in range [0.0, 1.0) <p>
	 * Normally,
	 * 0.0 is near dawn.
	 * 0.25 is midday.
	 * 0.5 is near dusk.
	 * 0.75 is midnight.
	 * @param tick the tick to get daytime offset.
	 * */
	public double getDaytimeOffset(long tick);
	
	/**
	 * Current time offset during a year. <p>
	 * Should be in range [0.0, 1.0) <p>
	 * 0.0 is midday.
	 * 0.25 is near dusk.
	 * 0.5 is midnight.
	 * 0.75 is near dawn.
	 * */
	public double getYearlyOffset();
	
	/**
	 * Current time offset during a year. <p>
	 * Should be in range [0.0, 1.0) <p>
	 * 0.0 is midday.
	 * 0.25 is near dusk.
	 * 0.5 is midnight.
	 * 0.75 is near dawn.
	 * @param tick the tick to get year offset.
	 * */
	public double getYearlyOffset(long tick);
	
	/**
	 * Gets current celestial angle. <p>
	 * Basically for WorldProvider. <p>
	 * @param worldTime current world time
	 * @param partialTicks the partial tick
	 * */
	public float calculateCelestialAngle(long worldTime, float partialTicks);
	
	/**
	 * Gets current sun height rate. <p>
	 * Basically for WorldProvider. <p>
	 * @param partialTicks the partial tick
	 * */
	public float calculateSunHeight(float partialTicks);
	
	/**
	 * Gets current sunlight factor. <p>
	 * Basically for WorldProvider. <p>
	 * @param partialTicks the partial tick
	 * */
	public float calculateSunlightFactor(float partialTicks);
	
	/**
	 * Gets current sunrise sunset factor(brightness of sunrise/sunset color). <p>
	 * Basically for WorldProvider. <p>
	 * @param partialTicks the partial tick
	 * */
	public float calculateSunriseSunsetFactor(float partialTicks);
	
	/**
<<<<<<< HEAD
=======
	 * Gets dispersion factor. (brightness of sky itself, not ground light level) <p>
	 * Basically for WorldProvider. <p>
	 * @param partialTicks the partial tick
	 * */
	public float calculateDispersionFactor(float partialTicks);
	
	/**
	 * Gets light pollution factor.
	 * Basically for WorldProvider. <p>
	 * @param partialTicks the partial tick
	 * */
	public float calculateLightPollutionFactor(float partialTicks);
	
	/**
	 * Gets current moon phase. <p>
	 * Basically for WorldProvider. <p>
	 * @param worldTime the current World Time. If it isn't, this method will give undefined result.
	 * */
	public int getCurrentMoonPhase(long worldTime);
	
	/**
	 * Gets current sunrise sunset factor(brightness of sunrise/sunset color). <p>
	 * Basically for WorldProvider. <p>
	 * */
	public float getCurrentMoonPhaseFactor();
	
	/**
	 * Gets height angle when sun is on highest today. <p>
	 * Useful for checking highest time. <p>
	 * Also can be used for seasons.
	 * */
	public double getHighestSunHeightAngle();
	
	/**
	 * Gets height angle when moon is on highest today. <p>
	 * Useful for checking highest time.
	 * */
	public double getHighestMoonHeightAngle();
	
	/**
	 * Daytime offset when sun reaches certain height angle first. <p>
	 * May give negative value or time on reflected position. <p>
	 * Also error gets bigger when year is short relative to day. <p>
	 * @param heightAngle the height angle in degrees.
	 * */
	public double dayOffsetUntilSunReach(double heightAngle);
	
	/**
	 * Day offset when moon reaches certain height angle first. <p>
	 * May give negative value or time on reflected position. <p>
	 * @param heightAngle the height angle in degrees.
	 * */
	public double dayOffsetUntilMoonReach(double heightAngle);
	
	/**
	 * Current position of sun.
	 * */
	public Vector3f getCurrentSunPosition();
	
	/**
	 * Current position of moon.
	 * */
	public Vector3f getCurrentMoonPosition();
	
	
	/**
	 * Gets per dimension location for certain resource id.
	 * @param resourceId id for this resource
	 * @return resource location on the dimension, or <code>null</code> if there is no specific resource location on the dimension for the id.
	 * */
	public ResourceLocation getPerDimensionResourceLocation(String resourceId);
	
}
