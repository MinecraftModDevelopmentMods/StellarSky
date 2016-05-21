package stellarium.stellars;

import net.minecraftforge.common.config.Configuration;
import stellarapi.api.lib.config.SimpleConfigHandler;
import stellarapi.api.lib.config.property.ConfigPropertyDouble;
import stellarium.StellarSky;
import stellarium.util.math.CachedGaussianRandom;

public class Optics extends SimpleConfigHandler {
	
	public static final double ext_coeff_B_V=0.1;
	public static final double ext_coeff_V=0.2;
	
	// MagOffset needs to be changed to whatever the maximum Magnitude of Venus is 
	private static final float magOffset = 5.50f;
	private static final float magCompressionBase = 6.50f;
		
	private static CachedGaussianRandom randomTurbulance = new CachedGaussianRandom(100, 3L);
	
	public static final Optics instance = new Optics();
	
	private ConfigPropertyDouble propBrightnessContrast;
	private ConfigPropertyDouble propTurb;
	
	private double brightnessContrast = 2.0;
	private float magCompression;
	private float magContrast;
	private double turbulance;
	
	public Optics() {
		this.propBrightnessContrast = new ConfigPropertyDouble("Brightness_Contrast", "", 2.0);
		this.propTurb = new ConfigPropertyDouble("Twinkling(Turbulance)", "", 1.0);
		
		this.addConfigProperty(this.propBrightnessContrast);
		this.addConfigProperty(this.propTurb);
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
		propBrightnessContrast.setMinValue(0.0);
		
        propTurb.setComment("Degree of the twinkling effect of star.\n"
        		+ "It determines the turbulance of atmosphere, which actually cause the twinkling effect. "
				+ "The greater the value, the more the stars will twinkle. Default is 1.0. To disable set to 0.0");
        propTurb.setRequiresMcRestart(false);
        propTurb.setLanguageKey("config.property.client.turbulance");
        propTurb.setMinValue(0.0);
        propTurb.setMaxValue(2.0);
	}

	@Override
	public void loadFromConfig(Configuration config, String category) {
		this.brightnessContrast = propBrightnessContrast.getDouble();
		this.turbulance = propTurb.getDouble() * 4.0;
		this.magCompression = magCompressionBase / StellarSky.proxy.getClientSettings().mag_Limit;
		this.magContrast = (float) Math.pow(2.512, (1.0/(brightnessContrast * magCompression)));
	}
	
	@Override
	public void saveToConfig(Configuration config, String category) { }
	
	public static float getAlphaFromMagnitudeSparkling(float Mag, float bglight){
		double turb = randomTurbulance.nextGaussian() * (instance.turbulance / (Mag + 4.46f));
		return getAlpha(((Mag + 1.46f) * instance.magCompression) + turb, bglight);
	}

	public static float getAlphaFromMagnitude(double Mag, float bglight) {
		return getAlpha(Mag, bglight);
	}
	
	public static float getAlphaForGalaxy(double Mag, float bglight) {
		return getAlpha(((Mag + 1.46f) * instance.magCompression), bglight);
	}
	
	public static final double constantBgDiv = Math.log(2.1333334f + 1.0f);
	
	private static float getAlpha(double magCompressed, float bglight) {
		return (float) ((Math.pow(instance.magContrast,	- magCompressed - magOffset * ((Math.pow(Math.log(bglight + 1.0f)/constantBgDiv, magOffset)))))	- (bglight / 2.1333334f));
	}
	
}