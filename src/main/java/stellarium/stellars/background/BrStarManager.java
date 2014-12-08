package stellarium.stellars.background;

import sciapi.api.value.euclidian.EVector;

public class BrStarManager extends StarManager{
	Star[] bgstars;

	@Override
	public Star[] GetStarArray(EVector view, double dangle) {
		return bgstars;
	}

}
