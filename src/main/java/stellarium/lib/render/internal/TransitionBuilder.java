package stellarium.lib.render.internal;

import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import stellarium.lib.render.hierarchy.IRenderState;
import stellarium.lib.render.hierarchy.IRenderTransition;
import stellarium.lib.render.hierarchy.IRenderedCollection;
import stellarium.lib.render.hierarchy.ITransitionBuilder;

public class TransitionBuilder implements ITransitionBuilder, IRenderTransition {
	
	private final Function infoTransformer;
	private final IRenderState initialState;
	private ImmutableMap.Builder<IRenderState, Pair<Function, RendererSettings>> stateMap = ImmutableMap.builder();
	private Map<IRenderedCollection, RendererSettings> colToSettings = Maps.newIdentityHashMap();
	
	public TransitionBuilder(Function infoTransformer, IRenderState initialState) {
		this.infoTransformer = infoTransformer;
		this.initialState = initialState;
	}

	@Override
	public ITransitionBuilder appendState(IRenderState state, Object pass, IRenderedCollection subDist) {
		return this.appendStateWithPassFn(state, Functions.constant(pass), subDist);
	}
	
	@Override
	public ITransitionBuilder appendStateWithPassFn(IRenderState state, Function pass, IRenderedCollection subDist) {
		RendererSettings settings;
		if(colToSettings.containsKey(subDist))
			settings = colToSettings.get(subDist);
		else {
			settings = new RendererSettings(subDist);
			colToSettings.put(subDist, settings);
		}
		
		stateMap.put(state, Pair.of(pass, settings));
		return this;
	}

	@Override
	public IRenderTransition build() {
		return new RenderingFSM(this.infoTransformer,
				this.initialState, stateMap.build(), ImmutableSet.copyOf(colToSettings.values()));
	}

}
