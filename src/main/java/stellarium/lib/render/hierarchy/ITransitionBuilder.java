package stellarium.lib.render.hierarchy;

import com.google.common.base.Function;

public interface ITransitionBuilder<Pass, ResRCI> {
	
	/**
	 * Appends state with constant pass.
	 * @param state the state to append
	 * @param pass the pass for the state
	 * @param subDist the sub-distribution
	 * @return <code>this</code>
	 * */
	public <ChildPass> ITransitionBuilder<Pass, ResRCI> appendState(
			IRenderState<Pass, ResRCI> state, ChildPass pass,
			IRenderedCollection subDist);
	
	/**
	 * Appends state with pass transition function.
	 * @param state the state to append
	 * @param pass the pass transition function for the state
	 * @param subDist the sub-distribution
	 * @return <code>this</code>
	 * */
	public <ChildPass> ITransitionBuilder<Pass, ResRCI> appendStateWithPassFn(
			IRenderState<Pass, ResRCI> state, Function<Pass, ChildPass> pass,
			IRenderedCollection subDist);

	/**
	 * Builds this transition builder.
	 * */
	public IRenderTransition build();

}