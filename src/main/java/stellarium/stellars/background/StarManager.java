package stellarium.stellars.background;

import sciapi.api.value.euclidian.EVector;


public abstract class StarManager {
	public abstract Star[] GetStarArray(EVector view, double dangle);
}
