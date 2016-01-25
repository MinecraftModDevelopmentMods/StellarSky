package foxie.calendar.api;

import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;

public interface ICalendarProvider {
   /**
    * Gets total sum of days in a year
    *
    * @return days in a year
    */
   int getDaysInYear();

   /**
    * Gets total sum of ticks that happen in a year
    *
    * @return ticks in a year
    */
   int getTicksPerYear();

   /**
    * Gets current UNSCALED world time (in ticks)
    *
    * @return world time
    */
   long getTime();

   /**
    * Gets a day in a given month. Unscaled
    *
    * @return day of the month
    */
   int getDay();

   /**
    * Sets a day in a given month. Unscaled
    *
    * @param newDay new date
    * @return self
    */
   ICalendarProvider setDay(int newDay);

   /**
    * Gets a scaled day in a given month.
    *
    * @return scaled day of the month
    */
   int getScaledDay();

   /**
    * Sets a scaled day in a given month.
    *
    * @param newDay new day
    * @return self
    */
   ICalendarProvider setScaledDay(int newDay);

   /**
    * Gets a month for a given time. Months are calculated from 0
    *
    * @return month of the year
    */
   int getMonth();

   ICalendarProvider setMonth(int newMonth);

   int getScaledMonth();

   ICalendarProvider setScaledMonth(int newMonth);

   /**
    * Gets a year. Years are calculated from 0
    *
    * @return year
    */
   int getYear();

   ICalendarProvider setYear(int newYear);

   /**
    * Calculates current hour. Day begins at 00:00 and ends at 23:59
    *
    * @return hour of the day
    */
   int getHour();

   ICalendarProvider setHour(int newHour);

   int getScaledHour();

   ICalendarProvider setScaledHour(int newHour);

   /**
    * Calculates current minute. Note that there are 50 minutes, but it is rescaled to 60 minutes per hour!
    *
    * @return minute of the day
    */
   int getMinute();

   /**
    * Sets minutes. UNSCALED!
    *
    * @param newMinutes
    */
   ICalendarProvider setMinute(int newMinutes);

   int getScaledMinute();

   ICalendarProvider setScaledMinute(int newMinute);

   int getScaledSecond();

   ICalendarProvider setScaledSecond(int newSecond);

   int getSecond();

   ICalendarProvider setSecond(int newSecond);

   ICalendarProvider addScaledSeconds(int seconds);

   ICalendarProvider addSeconds(int seconds);

   ICalendarProvider addScaledMinutes(int minutes);

   ICalendarProvider addMinutes(int minutes);

   ICalendarProvider addHours(int hours);

   ICalendarProvider addDays(int days);

   ICalendarProvider addMonths(int months);

   ICalendarProvider addYears(int years);

   int getDaysInMonth(int month);

   /*
    * These are used to create a new instance of these calendars
    */
   ICalendarProvider create(World world);

   ICalendarProvider create(WorldProvider provider);

   ICalendarProvider create(long time);

   int getNumberOfMonths();

   String[] getListOfMonthsString();

   ICalendarProvider copy();

   void apply(World world);
}
