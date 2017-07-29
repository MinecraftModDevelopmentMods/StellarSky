package stellarium.api;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class StellarSkyAPI {

	@CapabilityInject(IRendererHolder.class)
	public static final Capability<IRendererHolder> SKY_RENDER_HOLDER = null;
	
	private List<IWorldProviderReplacer> worldProviderReplacers = Lists.newArrayList();
	private IWorldProviderReplacer defaultReplacer;
	private Map<String, ISkyType> skyTypes = Maps.newHashMap();
	private List<ISkyRenderType> rendererTypes = Lists.newArrayList();
	
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
	 * Register sky type for certain world name.
	 * @param worldName the name for the world
	 * @param type the type of the sky
	 * */
	public static void registerSkyType(String worldName, ISkyType type) {
		INSTANCE.skyTypes.put(worldName, type);
	}
	
	/**
	 * Registers sky renderer type. <p>
	 * Note that this should be done on both side.
	 * @param rendererType the sky renderer type to register
	 * */
	public static void registerRendererType(ISkyRenderType rendererType) {
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
	 * Sets up and get the sky type for certain dimension.
	 * @param dimensionName the name of the world; only provided information on the world
	 * */
	public static ISkyType getSkyType(String dimensionName) {
		ISkyType type = INSTANCE.skyTypes.get(dimensionName);
		type = (type != null? type : new DefaultSkyType());
		
		if(type.needUpdate()) {
			for(ISkyRenderType renderType : INSTANCE.rendererTypes)
				if(renderType.acceptFor(dimensionName))
					type.addRenderType(renderType);
		}
		
		return type;
	}
	
	/**
	 * Gets renderer for certain option of sky renderer type.
	 * @param option the sky renderer type
	 * @param subRenderer renderer to be called for rendering celestial sphere
	 * */
	public static IAdaptiveRenderer getRendererFor(String option, IRenderHandler subRenderer) {
		for(ISkyRenderType type : INSTANCE.rendererTypes)
			if(type.getName().equals(option))
				return type.createSkyRenderer(subRenderer);
		return null;
	}
}
