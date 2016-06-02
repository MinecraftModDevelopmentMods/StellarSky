package stellarium.lib.render.hierarchy;

import com.google.common.base.Function;

import stellarium.lib.render.IGenericRenderer;

public interface IDistributionBuilder<Pass, RCI> {
	
	/**
	 * Gives transition builder for this type of distribution builder
	 * */
	public <ResRCI> ITransitionBuilder<Pass, ResRCI> transitionBuilder(
			Function<RCI, ResRCI> transformer, IRenderState<Pass, ResRCI> initialState);
	
	public IPassRenderer<Pass, RCI> build(IRenderTransition transition);

}