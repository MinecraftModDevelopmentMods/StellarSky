package stellarium.world;

import stellarapi.api.ISkyEffect;

public interface IStellarSkySet extends ISkyEffect {
	public boolean hideObjectsUnderHorizon();
}
