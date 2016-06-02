package stellarium.lib.render.hierarchy.internal;

import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Multimap;

import stellarium.lib.hierarchy.HierarchyDistributor;
import stellarium.lib.render.hierarchy.IPassRenderer;
import stellarium.lib.render.hierarchy.IRenderState;

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
 * > Based on state changes
 * */
public class RenderingFSM implements IPassRenderer {

	private Class<?> modelClass;

	private IRenderState initialState;
	private Map<IRenderState, Object> passMap;
	private Multimap<IRenderState, Pair<Object, IPassRenderer>> idRendererMap;

	@Override
	public void renderPass(Object model, Object pass, Object resInfo) {
		IRenderState state = initialState.transitionTo(pass, resInfo);
		while(state != null) {
			for(Pair<Object, IPassRenderer> idRenderer : idRendererMap.get(state)) {
				Iterator ite = HierarchyDistributor.INSTANCE.iteratorFor(
						model, idRenderer.getLeft());
				while(ite.hasNext())
					idRenderer.getRight().renderPass(ite.next(), passMap.get(state), resInfo);
			}
			state = state.transitionTo(pass, resInfo);
		}
	}
}