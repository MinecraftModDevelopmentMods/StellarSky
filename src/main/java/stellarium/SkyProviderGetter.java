package stellarium;

import stellarium.api.ISkyProvider;
import stellarium.api.ISkyProviderGetter;
import stellarium.stellars.StellarManager;

public class SkyProviderGetter implements ISkyProviderGetter {

	@Override
	public ISkyProvider getSkyProvider() {
		return StellarManager.getManager(StellarSky.proxy.getDefWorld());
	}

}
