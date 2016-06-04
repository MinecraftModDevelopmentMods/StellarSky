package stellarium.lib.render;

import java.util.Map;

import com.google.common.collect.Maps;

import stellarium.lib.render.hierarchy.IDistributionConfigurable;
import stellarium.lib.render.internal.DistributionConfigurable;
import stellarium.lib.render.internal.GenericRendererWrapper;

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

	private Map<Class<?>, GenericRendererWrapper> renderers = Maps.newHashMap();

	/**
	 * Evaluates renderer, creating one if there wasn't the matching renderer.
	 * Returned renderer is wrapped version of the renderer
	 * to be consistent through modification of renderers.
	 * */
	public <T> IGenericRenderer<?,?,T,?> evaluateRenderer(Class<T> modelClass) {
		return this.evaluateRendererInternal(modelClass);
	}
	
	private GenericRendererWrapper evaluateRendererInternal(Class<?> modelClass) {
		if(!renderers.containsKey(modelClass))
			renderers.put(modelClass, new GenericRendererWrapper(modelClass));

		return renderers.get(modelClass);
	}

	public void bind(Class<?> modelClass, IGenericRenderer renderer) {
		GenericRendererWrapper wrapper = this.evaluateRendererInternal(modelClass);
		wrapper.setWrappedRenderer(renderer);
	}

	public <Pass, RCI> IDistributionConfigurable<Pass, RCI> configureRender(Class<?> modelClass) {
		return new DistributionConfigurable(modelClass);
	}
}
