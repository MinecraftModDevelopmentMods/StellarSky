package stellarium.lib.render.internal;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import stellarium.lib.hierarchy.HierarchyDistributor;
import stellarium.lib.render.IGenericRenderer;
import stellarium.lib.render.hierarchy.IRenderState;
import stellarium.lib.render.hierarchy.IRenderTransition;

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
public class RenderingFSM implements IGenericRenderer, IRenderTransition {

	private Function infoTransformer;
	private IRenderState initialState;
	private Map<IRenderState, Pair<Object, RendererSettings>> stateMap;
	private Set<RendererSettings> setSettings;

	public RenderingFSM(Function infoTransformer, IRenderState initialState,
			ImmutableMap<IRenderState, Pair<Object, RendererSettings>> stateMap, ImmutableSet<RendererSettings> setSettings) {
		this.infoTransformer = infoTransformer;
		this.initialState = initialState;
		this.stateMap = stateMap;
		this.setSettings = setSettings;
	}

	@Override
	public void initialize(Object settings) {
		for(RendererSettings rset : this.setSettings) {
			Object subSettings = rset.settingsTransformer.apply(settings);
			for(IGenericRenderer renderer : rset.subModelRenderers.values()) {
				renderer.initialize(subSettings);
			}
		}
	}

	@Override
	public void preRender(Object settings, Object info) {
		Object resInfo = infoTransformer.apply(info);

		for(RendererSettings rset : this.setSettings) {
			Object subSettings = rset.settingsTransformer.apply(settings);
			for(IGenericRenderer renderer : rset.subModelRenderers.values()) {
				renderer.preRender(subSettings, resInfo);
			}
		}
	}

	@Override
	public void renderPass(Object model, Object pass, Object info) {
		Object resInfo = infoTransformer.apply(info);
		
		IRenderState state = initialState.transitionTo(pass, resInfo);
		while(state != null) {
			RendererSettings settings = stateMap.get(state).getRight();
			Object childPass = stateMap.get(state).getLeft();
			for(Map.Entry<Object, IGenericRenderer> idRenderer : settings.subModelRenderers.entrySet()) {
				Iterator ite = HierarchyDistributor.INSTANCE.iteratorFor(model, idRenderer.getKey());
				while(ite.hasNext())
					idRenderer.getValue().renderPass(ite.next(), childPass, resInfo);
			}
			state = state.transitionTo(pass, resInfo);
		}
	}

	@Override
	public void postRender(Object settings, Object info) {
		Object resInfo = infoTransformer.apply(info);

		for(RendererSettings rset : this.setSettings) {
			Object subSettings = rset.settingsTransformer.apply(settings);
			for(IGenericRenderer renderer : rset.subModelRenderers.values()) {
				renderer.postRender(subSettings, resInfo);
			}
		}
	}
}