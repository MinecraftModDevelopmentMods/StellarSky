package stellarium.api;

import java.util.List;

import com.google.common.collect.Lists;

import stellarium.StellarSky;

public class StellarSkyAPI {
	
	public IHourProvider hourProvider;
	
	private static StellarSkyAPI INSTANCE = new StellarSkyAPI();
	
	/**
	 * Registers Hour Provider. <p>
	 * Currently hour provider only have effect on client. <p>
	 * You can manually create an wrapper for previous provider.
	 * */
	public static void registerHourProvider(IHourProvider provider) {
		INSTANCE.hourProvider = provider;
	}
	
	public static IHourProvider getCurrentHourProvider() {
		return INSTANCE.hourProvider;
	}
	
	/**
	 * Gets Sky Provider. Effective on both side.
	 * */
	public static ISkyProvider getSkyProvider() {
		return StellarSky.getManager();
	}
	
}
