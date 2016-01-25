package stellarium.compat.calendarapi;

import foxie.calendar.api.CalendarAPI;
import stellarium.StellarSky;
import stellarium.compat.ICompatModule;

public class ModuleCalendarAPI implements ICompatModule {

	@Override
	public void onPreInit() { }

	@Override
	public void onInit() { }

	@Override
	public void onPostInit() {
		if(StellarSky.getManager().serverEnabled)
			CalendarAPI.registerCalendarProvider(0, new StellarCalendarProvider());
	}

}
