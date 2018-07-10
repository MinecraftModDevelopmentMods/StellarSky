package stellarium.world;

import stellarapi.api.view.IAtmosphereEffect;

public interface IStellarSkySet extends IAtmosphereEffect {
	public boolean hideObjectsUnderHorizon();
}
