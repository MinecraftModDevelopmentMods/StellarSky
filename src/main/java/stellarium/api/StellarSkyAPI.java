package stellarium.api;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.client.IRenderHandler;

public class StellarSkyAPI {
	
	private IHourProvider hourProvider;
	private List<IWorldProviderReplacer> worldProviderReplacers = Lists.newArrayList();
	private IWorldProviderReplacer defaultReplacer;
	private List<ISkyRendererType> rendererTypes = Lists.newArrayList();
	
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
	 * Registers sky renderer type.
	 * @param rendererType the sky renderer type to register
	 * */
	public static void registerRendererType(ISkyRendererType rendererType) {
		INSTANCE.rendererTypes.add(rendererType);
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
	 * Gets possible render types for certain dimension.
	 * @param worldName the name of the world; only provided information on the world
	 * */
	public static String[] getRenderTypesForDimension(String worldName) {
		List<String> strlist = Lists.newArrayList();
		for(ISkyRendererType type : INSTANCE.rendererTypes)
			if(type.acceptFor(worldName))
				strlist.add(type.getName());
		return strlist.toArray(new String[0]);
	}
	
	/**
	 * Gets renderer for certain option of sky renderer type.
	 * @param option the sky renderer type
	 * @param subRenderer renderer to be called for rendering celestial sphere
	 * */
	public static IRenderHandler getRendererFor(String option, ICelestialRenderer subRenderer) {
		for(ISkyRendererType type : INSTANCE.rendererTypes)
			if(type.getName().equals(option))
				return type.createSkyRenderer(subRenderer);
		return null;
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
