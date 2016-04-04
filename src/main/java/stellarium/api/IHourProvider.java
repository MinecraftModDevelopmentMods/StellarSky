package stellarium.api;

public interface IHourProvider {
	
	/**
	 * Gives current hour.
	 * @param dayLength length of day
	 * @param timeInDay current time in a tick during a day
	 * @return current hour to display
	 * */
	public int getCurrentHour(double dayLength, double timeInDay);
	
	/**
	 * Gives current minute.
	 * @param dayLength length of day
	 * @param timeInDay current time in a tick during a day
	 * @param hour current hour calculated
	 * @return current minute to display
	 * */
	public int getCurrentMinute(double dayLength, double timeInDay, int hour);
	
	/**
	 * Gives number of hour in a day.
	 * @param dayLength length of day
	 * @return number of hour in a day to display
	 * */
	public int getTotalHour(double dayLength);
	
	/**
	 * Gives number of minute in an hour.
	 * @param dayLength length of day
	 * @param totalHour number of hour in a day
	 * @return number of minute in a hour to display
	 * */
	public int getTotalMinute(double dayLength, int totalHour);

	/**
	 * Gives number of minute left from representing day in hour.
	 * @param dayLength length of day
	 * @param totalHour number of hour in a day
	 * @return number of minute left in a day to display
	 * */
	public int getRestMinuteInDay(double daylength, int totalhour);

}
