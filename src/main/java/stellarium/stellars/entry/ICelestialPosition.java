package stellarium.stellars.entry;

import sciapi.api.value.euclidian.EVector;

public interface ICelestialPosition {
	
	public int roughExponentScale();
	public EVector getRelativePosition();
	public void update(double year);

}
