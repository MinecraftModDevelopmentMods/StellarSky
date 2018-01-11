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
import stellarapi.api.world.worldset.WorldSet;

@Deprecated
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
	 * Will be removed on the next version.
	 * TODO Move to Stellar API
	 * @param replacer the world provider replacer to register
	 * */
	@Deprecated
	public static void registerWorldProviderReplacer(IWorldProviderReplacer replacer) {
		INSTANCE.worldProviderReplacers.add(replacer);
	}
	
	/**
	 * Sets default world provider replacer. <p>
	 * Should only used by Stellar Sky.
	 * Will be removed on the next version.
	 * @param defaultReplacer the default world provider replacer
	 * */
	@Deprecated
	public static void setDefaultReplacer(IWorldProviderReplacer defaultReplacer) {
		INSTANCE.defaultReplacer = defaultReplacer;
	}
	
	/**
	 * Register sky type for certain world name.
	 * Will be removed on the next version.
	 * @param worldName the name for the world
	 * @param type the type of the sky
	 * */
	@Deprecated
	public static void registerSkyType(String worldName, ISkyType type) {
		INSTANCE.skyTypes.put(worldName, type);
	}

	/**
	 * Registers sky renderer type. <p>
	 * Note that this should be done on both side.
	 * Will be removed on the next version.
	 * @param rendererType the sky renderer type to register
	 * */
	@Deprecated
	public static void registerRendererType(ISkyRenderType rendererType) {
		INSTANCE.rendererTypes.add(rendererType);
	}
	
	
	/**
	 * Gets replaced world provider.
	 * Will be removed on the next version.
	 * @param world the world to replace the provider
	 * @param originalProvider original provider to be replaced
	 * @param helper the celestial helper
	 * @return the provider which will replace original provider
	 * */
	@Deprecated
	public static WorldProvider getReplacedWorldProvider(World world, WorldProvider originalProvider, ICelestialHelper helper) {
		for(IWorldProviderReplacer replacer : INSTANCE.worldProviderReplacers)
			if(replacer.accept(world, originalProvider))
				return replacer.createWorldProvider(world, originalProvider, helper);
		
		return INSTANCE.defaultReplacer.createWorldProvider(world, originalProvider, helper);
	}
	
	/**
	 * Sets up and get the sky type for certain dimension.
	 * Will be removed on the next version.
	 * @param worldSet the given worldSet
	 * */
	@Deprecated
	public static ISkyType getSkyType(WorldSet worldSet) {
		ISkyType type = INSTANCE.skyTypes.get(worldSet);
		type = (type != null? type : new DefaultSkyType());
		
		if(type.needUpdate()) {
			for(ISkyRenderType renderType : INSTANCE.rendererTypes)
				if(renderType.acceptFor(worldSet))
					type.addRenderType(renderType);
		}
		
		return type;
	}
	
	/**
	 * Gets renderer for certain option of sky renderer type.
	 * Will be removed on the next version.
	 * @param option the sky renderer type
	 * @param subRenderer renderer to be called for rendering celestial sphere
	 * */
	@Deprecated
	public static IAdaptiveRenderer getRendererFor(String option, IRenderHandler subRenderer) {
		for(ISkyRenderType type : INSTANCE.rendererTypes)
			if(type.getName().equals(option))
				return type.createSkyRenderer(subRenderer);
		return null;
	}
}
