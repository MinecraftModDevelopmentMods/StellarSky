package stellarium.render;

import stellarium.api.ISkySetType;

public enum SkySetTypeEnd implements ISkySetType {
	INSTANCE;

	@Override
	public double getLatitude() { return -52.5; }
	@Override
	public double getLongitude() { return 180.0; }

	@Override
	public double getDispersionRate() { return 0.0; }

	@Override
	public double getSkyRenderBrightness() { return 0.3; }

	@Override
	public double[] getSkyExtinctionFactors() { return new double[] {0, 0, 0}; }

	@Override
	public boolean hideObjectsUnderHorizon() { return false; }
	@Override
	public boolean doesAllowExtinction() { return false; }

}
