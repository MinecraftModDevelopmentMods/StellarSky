package stellarium.api;

/**
 * Enumeration for sky properties.
 * The parameter class is the required output.
 * */
public enum EnumSkyProperty {
	Lattitude(double.class), Longitude(double.class),
	SkyExtinctionFactors(double[].class),
	SkyDispersionRate(double.class),
	SkyRenderBrightness(double.class),
	HideObjectsUnderHorizon(boolean.class),
	AllowRefraction(boolean.class);
	
	private Class<?> propertyType;
	
	EnumSkyProperty(Class<?> propertyType) {
		this.propertyType = propertyType;
	}
}
