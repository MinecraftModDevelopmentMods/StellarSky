package stellarium.lib.render.hierarchy;

/**
 * Representation for a render state, which should be immutable.
 * */
public interface IRenderState<Pass, ResRCI> {

	/**
	 * Transitions state <b>after</b> rendering.
	 * @param pass the pass which is given to the rendering distributor
	 * @param resInfo the information which is evaluated from the rendering distributor
	 * @return next state, or <code>null</code> to finalize rendering
	 * */
	public IRenderState<Pass, ResRCI> transitionTo(Pass pass, ResRCI resInfo);
	
	public boolean equals(Object obj);
	public int hashCode();

}