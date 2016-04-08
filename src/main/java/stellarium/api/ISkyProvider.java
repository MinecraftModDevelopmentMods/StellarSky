package stellarium.api;

import org.lwjgl.util.vector.Vector3f;

/**
 * Interface provided by stellar sky api.
 * Do not implement this!
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
	 * 0.0 is midday.
	 * 0.25 is near dusk.
	 * 0.5 is midnight.
	 * 0.75 is near dawn.
	 * */
	public double getDaytimeOffset();
	
	/**
	 * Current time offset during a day. <p>
	 * Should be in range [0.0, 1.0) <p>
	 * Normally,
	 * 0.0 is midday.
	 * 0.25 is near dusk.
	 * 0.5 is midnight.
	 * 0.75 is near dawn.
	 * @param tick the tick to get daytime offset.
	 * */
	public double getDaytimeOffset(long tick);
	
	/**
	 * Current time offset during a year. <p>
	 * Should be in range [0.0, 1.0) <p>
	 * 0.0 is middle of spring,
	 * 0.25 is middle of summer,
	 * 0.5 is middle of autumn,
	 * 0.75 is middle of winter.
	 * */
	public double getYearlyOffset();
	
	/**
	 * Current time offset during a year. <p>
	 * Should be in range [0.0, 1.0) <p>
	 * 0.0 is middle of spring,
	 * 0.25 is middle of summer,
	 * 0.5 is middle of autumn,
	 * 0.75 is middle of winter.
	 * @param tick the tick to get year offset.
	 * */
	public double getYearlyOffset(long tick);
	
	/**
	 * Gets height angle when sun is on highest today.
	 * Useful for checking highest time.
	 * Also can be used for seasons.
	 * */
	public double getHighestSunHeightAngle();
	
	/**
	 * Gets height angle when moon is on highest today.
	 * Useful for checking highest time.
	 * */
	public double getHighestMoonHeightAngle();
	
	/**
	 * Daytime offset when sun reaches certain height angle first.
	 * May give negative value or time on reflected position.
	 * Also error gets bigger when year is short relative to day.
	 * @param heightAngle the height angle in degrees.
	 * */
	public double dayOffsetUntilSunReach(double heightAngle);
	
	/**
	 * Day offset when moon reaches certain height angle first.
	 * May give negative value or time on reflected position.
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

}
