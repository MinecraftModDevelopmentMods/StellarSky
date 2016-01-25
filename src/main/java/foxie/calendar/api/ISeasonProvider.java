package foxie.calendar.api;

import net.minecraft.world.World;

public interface ISeasonProvider {
   /**
    * Gets a season for a given month
    *
    * @param calendar calendar date to fetch the season for
    * @return season
    */
   ISeason getSeason(ICalendarProvider calendar);

   /**
    * Get season progress (0 - 1), how far has the season progressed
    *
    * @param calendar calendar to calculate the progress for
    * @return progress (0 - 1)
    */
   float getSeasonProgress(ICalendarProvider calendar);

   /**
    * All the seasons! (used for sorting, look ups, listing etc)
    *
    * @return seasons
    */
   ISeason[] getAllSeasons();

   /**
    * Get temperature
    *
    * @param world world instance
    * @param x     xCoord
    * @param y     yCoord
    * @param z     zCoord
    * @return temperature in Kelvines
    */
   int getTemperature(World world, int x, int y, int z);
}
