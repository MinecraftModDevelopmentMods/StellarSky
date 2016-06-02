package stellarium.lib.render;

import java.util.Map;

import com.google.common.base.Predicates;
import com.google.common.collect.Maps;

import stellarium.lib.hierarchy.HierarchyDistributor;
import stellarium.lib.render.hierarchy.IRenderDistribution;
import stellarium.lib.render.hierarchy.internal.RenderDistribution;

/**
 * Settings: Just a rendering settings
 * Model: Maintains data and hierarchy
 * Pass: Defines render pass, defined by rendering distributors.
 *   - Type varies for each renderer
 * Render Context Information: Current information on rendering.
 *   - Might be manipulated in renderers itself
 *   - Type varies for each rendering distributor
 * State: Finite State for rendering distributor
 *   - Each state is linked with its own renderer and pass. (Stored in maps)
 *   - Transition can be defined in the state. (Gives next state)
 * > Renderer defines the rendering order
 * > Based on state changes.
 * */
public enum RendererRegistry {
	INSTANCE;

	private Map<Class<?>, IGenericRenderer> renderers = Maps.newHashMap();

	public <T> IGenericRenderer<?,?,T,?> getRenderer(Class<T> modelClass) {
		return renderers.get(modelClass);
	}

	public void bind(Class<?> modelClass, IGenericRenderer renderer) {
		renderers.put(modelClass, renderer);
	}

	public <ID> IRenderDistribution<ID> generateDistribution(Class<?> modelClass) {
		return new RenderDistribution(modelClass, Predicates.alwaysTrue());
	}
}
