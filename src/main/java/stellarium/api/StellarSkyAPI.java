package stellarium.api;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.client.IRenderHandler;

public class StellarSkyAPI {
	
	private List<IWorldProviderReplacer> worldProviderReplacers = Lists.newArrayList();
	private IWorldProviderReplacer defaultReplacer;
	private List<ISkyRendererType> rendererTypes = Lists.newArrayList();
	
	private static StellarSkyAPI INSTANCE = new StellarSkyAPI();
	
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
	 * Registers sky renderer type. <p>
	 * Note that this should be done on both side.
	 * @param rendererType the sky renderer type to register
	 * */
	public static void registerRendererType(ISkyRendererType rendererType) {
		INSTANCE.rendererTypes.add(rendererType);
	}
	
	
	/**
	 * Gets replaced world provider.
	 * @param world the world to replace the provider
	 * @param originalProvider original provider to be replaced
	 * @param helper the celestial helper
	 * @return the provider which will replace original provider
	 * */
	public static WorldProvider getReplacedWorldProvider(World world, WorldProvider originalProvider, ICelestialHelper helper) {
		for(IWorldProviderReplacer replacer : INSTANCE.worldProviderReplacers)
			if(replacer.accept(world, originalProvider))
				return replacer.createWorldProvider(world, originalProvider, helper);
		
		return INSTANCE.defaultReplacer.createWorldProvider(world, originalProvider, helper);
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
}
