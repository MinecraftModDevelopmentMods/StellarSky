package foxie.calendar.api;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarAPI {
   public static final String MODNAME = "Calendar API";
   public static final String VERSION = "1.0";

   private static Map<Integer, ISeasonProvider>   seasonProviders;
   private static Map<Integer, ICalendarProvider> calendarProviders;

   static {
      seasonProviders = new HashMap<Integer, ISeasonProvider>();
      calendarProviders = new HashMap<Integer, ICalendarProvider>();
   }

   /**
    * Gets a season provider for a given dimension
    *
    * @param dimensionId dimension
    * @return season provider
    */
   public static ISeasonProvider getSeasonProvider(int dimensionId) {
      return seasonProviders.get(dimensionId);
   }

   /**
    * Gets a season provider for dimension 0 (overworld)
    *
    * @return season provider for the overworld
    */
   public static ISeasonProvider getSeasonProvider() {
      return getSeasonProvider(0);
   }

   /**
    * Adds (registers/overrides) a season provider for a given dimensionId
    *
    * @param dimensionId dimension Id to register it to
    * @param provider    provider itself
    */
   public static void registerSeasonProvider(int dimensionId, ISeasonProvider provider) {
      if (seasonProviders.get(dimensionId) != null) {
         FMLLog.info("[" + MODNAME + "] Season provider already registered! Replacing on request then by mod " +
                 Loader.instance().activeModContainer().getModId());
      }

      seasonProviders.put(dimensionId, provider);
   }

   /**
    * Adds (registers/overrides) a calendar provider for a given dimensionId
    *
    * @param dimensionId dimension Id to register it to
    * @param provider    provider itself
    */
   public static void registerCalendarProvider(int dimensionId, ICalendarProvider provider) {
      if (calendarProviders.get(dimensionId) != null) {
         FMLLog.info("[" + MODNAME + "] Calendar provider already registered for dimension " + dimensionId +
                 "! Replacing on request then by mod " + Loader.instance().activeModContainer().getModId());
      }

      calendarProviders.put(dimensionId, provider);
   }

   /**
    * Gets a calendar instance
    *
    * @param time        time
    * @param dimensionId dimension
    * @return calendar
    */
   public static ICalendarProvider getCalendarInstance(long time, int dimensionId) {
      if (calendarProviders.containsKey(dimensionId))
         return calendarProviders.get(dimensionId).create(time);

      return calendarProviders.get(0).create(time);
   }

   /**
    * Gets a calendar instance for a world
    *
    * @param world world
    * @return calendar
    */
   public static ICalendarProvider getCalendarInstance(World world) {
      if (calendarProviders.containsKey(world.provider.dimensionId))
         return calendarProviders.get(world.provider.dimensionId).create(world);

      return calendarProviders.get(0).create(world);
   }

   /**
    * Gets a calendar instance for a world provider
    *
    * @param provider world provider
    * @return calendar
    */
   public static ICalendarProvider getCalendarInstance(WorldProvider provider) {
      if (calendarProviders.containsKey(provider.dimensionId))
         return calendarProviders.get(provider.dimensionId).create(provider);

      return calendarProviders.get(0).create(provider);
   }

   /**
    * Gets a calendar instance for the overworld
    *
    * @param time time of the calendar
    * @return calendar
    */
   public static ICalendarProvider getCalendarInstance(long time) {
      return getCalendarInstance(time, 0);
   }

   /**
    * Gets a calendar instance for the overworld at time 0
    *
    * @return calendar
    */
   public static ICalendarProvider getCalendarInstance() {
      return getCalendarInstance(0);
   }

   /**
    * @param provider  Calendar to look up descriptors for
    * @param tolerance time tolerance of the request
    * @return list of descriptors
    */
   public static List<DayTimeDescriptor> getDayTimeDescriptors(ICalendarProvider provider, int tolerance) {
      return provider.getDayTimeDescriptors(tolerance);
   }
}
