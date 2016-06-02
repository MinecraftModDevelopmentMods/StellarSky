package stellarium.lib.render.hierarchy;

public interface ITransitionBuilder<Pass, ResRCI> {

	/**
	 * Appends state.
	 * @param state the state to append
	 * @param pass the pass for the state
	 * @param subDist the sub-distribution
	 * @return <code>this</code>
	 * */
	public <ChildPass> ITransitionBuilder<Pass, ResRCI> appendState(
			IRenderState<Pass, ResRCI> state, ChildPass pass,
			IRenderDistribution subDist);

	/**
	 * Builds this transition builder.
	 * */
	public IRenderTransition build();

}