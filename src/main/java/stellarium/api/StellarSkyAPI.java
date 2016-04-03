package stellarium.api;

import net.minecraft.world.World;

public class StellarSkyAPI {
	
	public ISkyProviderGetter skyProviderGetter;
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
	 * Gets Sky Provider. Effective on both side.<p>
	 * NOTE: This do no work when world is not open.
	 * */
	public static ISkyProvider getSkyProvider(World world) {
		return INSTANCE.skyProviderGetter.getSkyProvider(world);
	}
	
	/**
	 * Internal method, do not use this!
	 * Will be removed in the next version.
	 * */
	@Deprecated
	public static void setSkyProviderGetter(ISkyProviderGetter providerGetter) {
		INSTANCE.skyProviderGetter = providerGetter;
	}
	
}
