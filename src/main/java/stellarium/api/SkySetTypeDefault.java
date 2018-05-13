package stellarium.api;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public enum SkySetTypeDefault implements ISkySetType {
	INSTANCE;

	@Override
	public double getLatitude() { return 37.5; }
	@Override
	public double getLongitude() { return 0.0; }

	@Override
	public double getDispersionRate() { return 1.0; }

	@Override
	public double getSkyRenderBrightness() { return 0.2; }

	@Override
	public double[] getSkyExtinctionFactors() { return new double[] {0.1, 0.2, 0.35}; }

	@Override
	public boolean hideObjectsUnderHorizon() { return true; }
	@Override
	public boolean doesAllowExtinction() { return true; }
}