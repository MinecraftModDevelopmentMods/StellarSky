package foxie.calendar.api;


import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.world.World;


public class DateTimeEvent extends Event {
   private World             world;
   private ICalendarProvider provider;

   public DateTimeEvent(World world, ICalendarProvider provider) {
      this.world = world;
      this.provider = provider;
   }

   public World getWorld() {
      return world;
   }

   public ICalendarProvider getOldCalendar() {
      return provider;
   }

   /**
    * New day event - fired when a day changes over (by internal time, NOT scaled time!)
    */
   public static class NewDayEvent extends DateTimeEvent {
      public NewDayEvent(World world, ICalendarProvider provider) {
         super(world, provider);
      }
   }

   /**
    * New month event - fired when a day changes over (by internal time, NOT scaled time!)
    */
   public static class NewMonthEvent extends DateTimeEvent {
      public NewMonthEvent(World world, ICalendarProvider provider) {
         super(world, provider);
      }
   }

   /**
    * New year event - fired when a year changes over (by internal time, NOT scaled time!)
    */
   public static class NewYearEvent extends DateTimeEvent {
      public NewYearEvent(World world, ICalendarProvider provider) {
         super(world, provider);
      }
   }

   /**
    * New season event - fired when a season changes over (by internal time, NOT scaled time!)
    */
   public static class NewSeasonEvent extends DateTimeEvent {
      private ISeason season;

      public NewSeasonEvent(World world, ICalendarProvider provider, ISeason season) {
         super(world, provider);
         this.season = season;
      }

      public ISeason getOldSeason() {
         return this.season;
      }
   }
}
