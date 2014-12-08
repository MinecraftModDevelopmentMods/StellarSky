package stellarium.stellars.background;

import sciapi.api.value.euclidian.EVector;

public abstract class DSObjManager {
	public abstract DSObj[] GetDsObjArray(EVector view, double dangle);
}
