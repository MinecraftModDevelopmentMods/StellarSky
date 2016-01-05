package stellarium.stellars;

import java.util.Random;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import stellarium.StellarSky;
import stellarium.config.IConfigHandler;

public class Optics implements IConfigHandler {
	
	// MagOffset needs to be changed to whatever the maximum Magnitude of Venus is 
	private static final float magOffset = 5.25f;
	private static final float magCompressionBase = 6.50f;
	
	private Property brightnessContrastProperty;
	
	//BrightnessContrast needs to be added to the user config and set to StellarSky.getManager().brightnesscontrast
	private double brightnessContrast = 2.0;
	private float magCompression;
	private float magContrast;
	
	private static Random randomTurbulance = new Random(3L);
	
	public static final Optics instance = new Optics();
	
	@Override
	public void setupConfig(Configuration config, String category) {
		brightnessContrastProperty = config.get(category, "Brightness_Contrast", 2.0);
		brightnessContrastProperty.comment = "Brightness Contrast determines the contrast "
				+ "between bright stars and faint stars. "
				+ "The bigger the value, the less difference between bright stars and faint stars. "
				+ "Real world (minimum) = 1.0. Default = 2.0 for visual effect.";
		brightnessContrastProperty.setRequiresMcRestart(false);
		brightnessContrastProperty.setLanguageKey("config.property.client.brcontrast");
	}

	@Override
	public void loadFromConfig(Configuration config, String category) {
		this.brightnessContrast = brightnessContrastProperty.getDouble();
		this.magCompression = magCompressionBase / StellarSky.getManager().mag_Limit;
		this.magContrast = (float) Math.pow(2.512, (1.0/(brightnessContrast * magCompression)));
	}
	
	public static float getAlphaFromMagnitudeSparkling(float Mag, float bglight){
		double turb = randomTurbulance.nextGaussian() * ((StellarSky.getManager().turb) / (Mag + 4.46f));
		return getAlpha(((Mag + 1.46f) * instance.magCompression) + turb, bglight);
	}

	public static float getAlphaFromMagnitude(double Mag, float bglight) {
		return getAlpha(Mag * instance.magCompression, bglight);
//		return getAlpha(0, bglight);
	}
	
	public static final double constantBgDiv = Math.log(2.1333334f + 1.0f);
	
	private static float getAlpha(double magCompressed, float bglight) {
		return (float) ((Math.pow(instance.magContrast,	- magCompressed - magOffset * ((Math.pow(Math.log(bglight + 1.0f)/constantBgDiv, magOffset)))))	- (bglight / 2.1333334f));
	}
	
}
