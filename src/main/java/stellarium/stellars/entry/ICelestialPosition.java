package stellarium.stellars.entry;

import sciapi.api.value.euclidian.ECoord;
import sciapi.api.value.euclidian.EVector;

public interface ICelestialPosition {
	
	/**
	 * Exponential scale of the position.
	 * gives X when the scale is roughly 10^X m.
	 * */
	public int roughExponentScale();
	
	/**
	 * Gives relative position from parent.
	 * */
	public EVector getRelativePosition();
	
	/**
	 * Base coordinate of the position.
	 * */
	public ECoord getPositionCoord();
	
	/**
	 * Update method.
	 * */
	public void update(double year);

}
