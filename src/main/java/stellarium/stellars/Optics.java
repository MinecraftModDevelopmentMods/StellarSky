package stellarium.stellars;

import java.util.Random;

import stellarium.StellarSky;

public class Optics {
	
	static float MagContrast;
	static float MagOffset;
	static float BrightnessContrast;
	static float MagLimit;
	static float MagCompression;
	static float MagCompressed;
	static float MagTurb;
	
	
	public static float getAlphaFromMagnitude(float Mag, float bglight){

		BrightnessContrast = 1.75f;
		//BrightnessContrast needs to be added to the user config and set to StellarSky.getManager().brightnesscontrast
		MagContrast = (float) ((float) ((Math.pow(2.512, (1/(BrightnessContrast * MagCompression))))));
		MagOffset = 5.0f;
		// MagOffset needs to be changed to whatever the maximum Magnitude of Venus is 
		MagLimit = StellarSky.getManager().mag_Limit;
		MagCompression = (float) 6.5f/MagLimit;

		Random randomno = new Random();
		float NewTurb = StellarSky.getManager().turb * (5.0f * (float) randomno.nextFloat());

		MagTurb = (float) ((NewTurb / (Mag + 3.46f)));	
		MagCompressed = (float) ((Mag * MagCompression) + MagTurb);
		
		return (float) ((Math.pow(MagContrast, MagCompressed * -1.0f)) / (Math.pow(MagContrast, MagOffset * ((Math.pow(Math.log(bglight + 1.0f), MagOffset) / (Math.pow(Math.log(2.1333334f + 1.0f), MagOffset)))))) - (bglight / 2.1333334f)) ;
		
		
	}

	public static float getAlphaFromMagnitude(double Mag, float bglight) {

		BrightnessContrast = 1.75f;
		MagContrast = (float) ((float) ((Math.pow(2.512, (1/(BrightnessContrast * MagCompression))))));
		MagOffset = 5.0f;
		MagLimit = StellarSky.getManager().mag_Limit;
		MagCompression = (float) 6.5f/MagLimit;
		MagCompressed = (float) ((Mag * MagCompression));
				
		return (float) ((Math.pow(MagContrast, MagCompressed * -1.0f)) / (Math.pow(MagContrast, MagOffset * ((Math.pow(Math.log(bglight + 1.0f), MagOffset) / (Math.pow(Math.log(2.1333334f + 1.0f), MagOffset)))))) - (bglight / 2.1333334f)) ;
		
	}
	
}
