package stellarium.stellars;

import net.minecraftforge.common.config.Configuration;
import stellarapi.api.lib.config.SimpleConfigHandler;
import stellarapi.api.lib.config.property.ConfigPropertyDouble;
import stellarapi.api.lib.math.Spmath;
import stellarium.util.math.CachedGaussianRandom;

public class OpticsHelper extends SimpleConfigHandler {
	public static final double ext_coeff_B=0.3;
	public static final double ext_coeff_V=0.2;
	public static final double ext_coeff_R=0.1;
	
	// MagOffset needs to be changed to whatever the maximum Magnitude of Venus is 
	//private static final float magOffset = 5.50f;
	private static final float magnitudeBase = 2.512f;
	//private static final float magCompressionBase = 6.50f;

	private static final double sunMagnitude = -26.74;
	private static final double upperMagLimit = -1.0;
	
	private static CachedGaussianRandom randomTurbulance = new CachedGaussianRandom(100, 3L);
	
	public static final OpticsHelper instance = new OpticsHelper();
	
	private ConfigPropertyDouble propBrightnessContrast;
	private ConfigPropertyDouble propTurb;
	private ConfigPropertyDouble propSpriteScale;
	
	private double brightnessContrast = 2.0;
	//private float magCompression;
	//private float magContrast;
	private float brightnessPower;
	private double turbulance;
	private float invSpriteScale;
	private double invSpriteScale2;

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
		
		this.brightnessContrast = propBrightnessContrast.getDouble();
		this.turbulance = propTurb.getDouble() * 4.0;
		this.brightnessPower = 1.0f / ((float)this.brightnessContrast);
		this.invSpriteScale = 1.0f / (float)propSpriteScale.getDouble();
		this.invSpriteScale2 = 1.0 / Spmath.sqr(propSpriteScale.getDouble());
	}
	
	@Override
	public void saveToConfig(Configuration config, String category) { }
	
	/*public static float getAlphaFromMagnitudeSparkling(float Mag, float bglight){
		double turb = randomTurbulance.nextGaussian() * (instance.turbulance / (Mag + 4.46f));
		return getAlpha(((Mag + upperMagLimit) * instance.magCompression) + turb, bglight);
	}

	public static float getAlphaFromMagnitude(double Mag, float bglight) {
		return getAlpha(Mag, bglight);
	}
	
	public static float getAlphaForGalaxy(double Mag, float bglight) {
		return getAlpha(((Mag + upperMagLimit) * instance.magCompression), bglight);
	}*/

	/*public static final double constantBgDiv = Math.log(2.1333334f + 1.0f);

	private static float getAlpha(double magCompressed, float bglight) {
		return (float) ((Math.pow(instance.magContrast,
				- magCompressed - magOffset *
				((Math.pow(Math.log(bglight + 1.0f)/constantBgDiv, magOffset)))))
				- (bglight / 2.1333334f));
	}*/
	
	public static final float invSpriteScalef() {
		return instance.invSpriteScale;
	}
	
	public static final double invSpriteScale2() {
		return instance.invSpriteScale2;
	}

	public static float turbulance() {
		return (float) (instance.turbulance * instance.randomTurbulance.nextGaussian() * 0.1);
	}

	public static float getBrightnessFromMagnitude(double magnitude) {
		return (float) Math.pow(magnitudeBase, upperMagLimit - magnitude);
	}
	
	public static float getDominationFromMagnitude(double magnitude) {
		return (float) Math.pow(magnitudeBase, sunMagnitude - magnitude);
	}

	public static float getCompressed(float brightness) {
		return (float) Math.pow(brightness, instance.brightnessPower);
	}
}