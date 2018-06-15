package stellarium.stellars;

import net.minecraftforge.common.config.Configuration;
import stellarapi.api.lib.config.SimpleConfigHandler;
import stellarapi.api.lib.config.property.ConfigPropertyDouble;
import stellarapi.api.lib.math.Spmath;
import stellarium.util.math.CachedGaussianRandom;

public class OpticsHelper extends SimpleConfigHandler {
	// Magnitude Base
	private static final double MAG_BASE = Math.pow(10.0, 0.4);

	// Magnitude of the Sun
	private static final double MAG_SUN = -26.74;

	// Default resolution in rad
	public static final float DEFAULT_RESOLUTION = Spmath.Radians(0.06f);

	// Magnitude of star with maximal brightness(intensity) 1.0 with default resolution
	private static final double MAG_UPPER_LIMIT = -1.0;
	// TODO Configurable Upper Magnitude Limit

	// Relative brightness of an object with size of 1 (rad)^2 compared to the star with default resolution (same flux)
	private static final float SURF_MULTIPLIER = (float) (2 * Math.PI * Spmath.sqr(DEFAULT_RESOLUTION));

	public static final OpticsHelper instance = new OpticsHelper();

	private CachedGaussianRandom randomTurbulance = new CachedGaussianRandom(100, 3L);

	@Deprecated
	private ConfigPropertyDouble propBrightnessContrast;
	private ConfigPropertyDouble propTurb;
	@Deprecated
	private ConfigPropertyDouble propSpriteScale;

	private double turbulance;

	public OpticsHelper() {
		this.propBrightnessContrast = new ConfigPropertyDouble("Brightness_Contrast", "", 2.0);
		this.propTurb = new ConfigPropertyDouble("Twinkling(Turbulance)", "", 1.0);
		this.propSpriteScale = new ConfigPropertyDouble("Sprite_Scale", "", 0.8);

		this.addConfigProperty(this.propBrightnessContrast);
		this.addConfigProperty(this.propTurb);
		this.addConfigProperty(this.propSpriteScale);
	}

	@Override
	public void setupConfig(Configuration config, String category) {
		config.setCategoryLanguageKey(category, "config.category.optics");
		config.setCategoryComment(category, "Configuration for Optical settings.");
		config.setCategoryRequiresMcRestart(category, false);

		super.setupConfig(config, category);

		propBrightnessContrast.setComment("Brightness Contrast determines the contrast "
				+ "between bright stars and faint stars. "
				+ "The bigger the value, the less difference between bright stars and faint stars. "
				+ "Real world (minimum) = 1.0. Default = 2.0 for visual effect.");
		propBrightnessContrast.setRequiresMcRestart(false);
		propBrightnessContrast.setLanguageKey("config.property.client.brcontrast");
		propBrightnessContrast.setMaxValue(4.0);
		propBrightnessContrast.setMinValue(0.5);

		propTurb.setComment("Degree of the twinkling effect of star.\n"
				+ "It determines the turbulance of atmosphere, which actually cause the twinkling effect. "
				+ "The greater the value, the more the stars will twinkle. Default is 1.0. To disable set to 0.0");
		propTurb.setRequiresMcRestart(false);
		propTurb.setLanguageKey("config.property.client.turbulance");
		propTurb.setMinValue(0.0);
		propTurb.setMaxValue(2.0);

		propSpriteScale.setComment("Sprite Scale determines the size of stars and planets. "
				+ "The bigger the value, the fuzzier stars/planets gets. "
				+ "Real world = 1.0. Default = 0.8 for visual effect.");
		propSpriteScale.setRequiresMcRestart(false);
		propSpriteScale.setLanguageKey("config.property.client.spritescale");
		propSpriteScale.setMaxValue(1.2);
		propSpriteScale.setMinValue(0.4);
	}

	@Override
	public void loadFromConfig(Configuration config, String category) {
		super.loadFromConfig(config, category);

		//this.brightnessContrast = propBrightnessContrast.getDouble();
		this.turbulance = propTurb.getDouble() * 4.0;
		//this.invSpriteScale = 1.0f / (float)propSpriteScale.getDouble();
		//this.invSpriteScale2 = 1.0 / Spmath.sqr(propSpriteScale.getDouble());
	}

	@Override
	public void saveToConfig(Configuration config, String category) { }

	public static float turbulance() {
		return (float) (instance.turbulance * instance.randomTurbulance.nextGaussian() * 0.1);
	}

	public static float getBrightnessFromMag(double magnitude) {
		return (float) Math.pow(MAG_BASE, MAG_UPPER_LIMIT - magnitude);
	}

	/** Gets multiplier for an object spread in area((rad)^2) */
	public static float getMultFromArea(double angularArea) {
		return SURF_MULTIPLIER / (float)angularArea;
	}

	public static float getDominationFromMag(double magnitude) {
		return (float) Math.pow(MAG_BASE, MAG_SUN - magnitude);
	}

	public static double getMultFromMag(double magnitude) {
		return Math.pow(MAG_BASE, -magnitude);
	}

	public static double getMagFromMult(double multiplier) {
		return - 2.5 * Math.log10(multiplier);
	}
}