package stellarium.api;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;

public class StellarSkyAPI {
	
	private IHourProvider hourProvider;
	private List<IWorldProviderReplacer> worldProviderReplacers = Lists.newArrayList();
	private IWorldProviderReplacer defaultReplacer;
	
	private static StellarSkyAPI INSTANCE = new StellarSkyAPI();
	
	/**
	 * Registers hour provider. <p>
	 * Currently hour provider only have effect on client. <p>
	 * You can manually create an wrapper for previous provider.
	 * @param provider the hour provider to register
	 * */
	public static void registerHourProvider(IHourProvider provider) {
		INSTANCE.hourProvider = provider;
	}
	
	/** Gets current hour provider. */
	public static IHourProvider getCurrentHourProvider() {
		return INSTANCE.hourProvider;
	}
	
	/**
	 * Registers world provider replacer.
	 * @param replacer the world provider replacer to register
	 * */
	public static void registerWorldProviderReplacer(IWorldProviderReplacer replacer) {
		INSTANCE.worldProviderReplacers.add(replacer);
	}
	
	/**
	 * Sets default world provider replacer. <p>
	 * Should only used by Stellar Sky.
	 * @param defaultReplacer the default world provider replacer
	 * */
	@Deprecated
	public static void setDefaultReplacer(IWorldProviderReplacer defaultReplacer) {
		INSTANCE.defaultReplacer = defaultReplacer;
	}
	
	/**
	 * Gets replaced world provider.
	 * @param world the world to replace the provider
	 * @param originalProvider original provider to be replaced
	 * @return the provider which will replace original provider
	 * */
	public static WorldProvider getReplacedWorldProvider(World world, WorldProvider originalProvider) {
		for(IWorldProviderReplacer replacer : INSTANCE.worldProviderReplacers)
			if(replacer.accept(world, originalProvider))
				return replacer.createWorldProvider(world, originalProvider);
		
		return INSTANCE.defaultReplacer.createWorldProvider(world, originalProvider);
	}
	
	/**
	 * Checks if there is sky provider for specific world.
	 * @param world the world to check if sky provider exists for
	 * @return <code>true</code> if there exists sky provider for this world.
	 * */
	public static boolean hasSkyProvider(World world) {
		return world.provider instanceof IStellarWorldProvider;
	}
		
	/**
	 * Gets the sky provider for specific world.
	 * @param world the world to get sky provider
	 * @return the sky provider for this world if it exists, <code>null</code> otherwise.
	 * */
	public static ISkyProvider getSkyProvider(World world) {
		if(hasSkyProvider(world))
			return ((IStellarWorldProvider)world.provider).getSkyProvider();
		else return null;
	}
	
}
