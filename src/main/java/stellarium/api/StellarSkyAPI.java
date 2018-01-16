package stellarium.api;

import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import net.minecraftforge.client.IRenderHandler;
import stellarapi.api.render.IAdaptiveRenderer;
import stellarapi.api.world.worldset.WorldSet;

/**
 * Placeholder for now.
 * */
@Deprecated
public class StellarSkyAPI {
	private Map<WorldSet, ISkySetType> skyTypes = Maps.newIdentityHashMap();
	private Map<String, ISkyRenderType> rendererTypes = Maps.newHashMap();
	private Map<WorldSet, ISkyRenderType> defRenderTypes = Maps.newIdentityHashMap();

	private static StellarSkyAPI INSTANCE = new StellarSkyAPI();

	/**
	 * Register sky set for certain world name.
	 * @param worldName the name for the world
	 * @param type the type of the sky
	 * */
	public static void registerSkyType(WorldSet worldSet, ISkySetType type) {
		INSTANCE.skyTypes.put(worldSet, type);
	}

	public static void registerDefaultRenderer(WorldSet worldSet, ISkyRenderType renderType) {
		INSTANCE.defRenderTypes.put(worldSet, renderType);
	}

	/**
	 * Registers sky renderer type. <p>
	 * Note that this should be done on both side.
	 * @param rendererType the sky renderer type to register
	 * */
	public static void registerRendererType(ISkyRenderType rendererType) {
		INSTANCE.rendererTypes.put(rendererType.getName(), rendererType);
	}

	/**
	 * Sets up and get the sky type for certain dimension.
	 * Will be removed on the next version.
	 * @param worldSet the given worldSet
	 * */
	public static ISkySetType getSkyType(WorldSet worldSet) {
		ISkySetType type = INSTANCE.skyTypes.get(worldSet);
		return (type != null? type : SkySetTypeDefault.INSTANCE);
	}

	public static ImmutableList<ISkyRenderType> possibleRenderTypes(WorldSet worldSet) {
		ImmutableList.Builder<ISkyRenderType> renderTypes = ImmutableList.builder();
		for(ISkyRenderType renderType : INSTANCE.rendererTypes.values()) {
			if(renderType.acceptFor(worldSet))
				renderTypes.add(renderType);
		}
		return renderTypes.build();
	}

	public static ISkyRenderType getDefaultRenderer(WorldSet worldSet) {
		return INSTANCE.defRenderTypes.get(worldSet);
	}

	/**
	 * Gets renderer for certain option of sky renderer type.
	 * Will be removed on the next version.
	 * @param option the sky renderer type
	 * @param subRenderer renderer to be called for rendering celestial sphere
	 * */
	public static IAdaptiveRenderer getRendererFor(String option, IRenderHandler subRenderer) {
		return INSTANCE.rendererTypes.get(option).createSkyRenderer(subRenderer);
	}
}
