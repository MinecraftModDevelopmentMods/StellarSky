package stellarium.lib.render.internal;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Predicates;

import stellarium.lib.hierarchy.HierarchyDistributor;
import stellarium.lib.render.RendererRegistry;
import stellarium.lib.render.hierarchy.IDistributionConfigurable;
import stellarium.lib.render.hierarchy.IRenderState;
import stellarium.lib.render.hierarchy.IRenderTransition;
import stellarium.lib.render.hierarchy.IRenderedCollection;
import stellarium.lib.render.hierarchy.ITransitionBuilder;

public class DistributionConfigurable implements IDistributionConfigurable {
	
	private Class<?> modelClass;

	public DistributionConfigurable(Class<?> modelClass) {
		this.modelClass = modelClass;
	}

	@Override
	public IRenderedCollection overallCollection() {
		return new RenderedCollection(this.modelClass, Predicates.alwaysTrue(), Functions.identity());
	}

	@Override
	public ITransitionBuilder transitionBuilder(Function transformer, IRenderState initialState) {
		return new TransitionBuilder(transformer, initialState);
	}

	@Override
	public void endSetup(IRenderTransition transition) {
		if(transition instanceof RenderingFSM)
			RendererRegistry.INSTANCE.bind(this.modelClass, (RenderingFSM)transition);
	}

}
