package stellarium.api;

import com.google.common.collect.ImmutableList;

/**
 * Type of sky default settings.
 * */
public interface ISkySetType {
	public double getLatitude();
	public double getLongitude();
	public double getDispersionRate();
	public double getSkyRenderBrightness();
	/** Gives color array with r,g,b components */
	public double[] getSkyExtinctionFactors();
	public boolean hideObjectsUnderHorizon();
	public boolean doesAllowExtinction();
}