package stellarium.lib.render.hierarchy;

import com.google.common.base.Function;

import stellarium.lib.render.IGenericRenderer;

public interface IDistributionConfigurable<Pass, RCI> {
	
	/**
	 * Creates and returns overall collection on the id.
	 * */
	public <SubID> IRenderedCollection<SubID> overallCollection();
	
	/**
	 * Gives transition builder for this type of distribution builder
	 * */
	public <ResRCI> ITransitionBuilder<Pass, ResRCI> transitionBuilder(
			Function<RCI, ResRCI> transformer, IRenderState<Pass, ResRCI> initialState);
	
	public void endSetup(IRenderTransition transition);

}