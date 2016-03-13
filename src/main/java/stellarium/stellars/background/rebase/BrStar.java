package stellarium.stellars.background.rebase;

import sciapi.api.value.IValRef;
import sciapi.api.value.euclidian.EVector;
import stellarium.StellarSky;
import stellarium.stellars.Optics;
import stellarium.stellars.base.IPerDimensionCache;
import stellarium.stellars.base.StellarObject;
import stellarium.stellars.util.ExtinctionRefraction;
import stellarium.stellars.view.IStellarViewpoint;
import stellarium.util.math.Rotate;
import stellarium.util.math.SpCoord;
import stellarium.util.math.Spmath;

public class BrStar extends StellarObject {
		
	//Zero-time axial tilt
	public static final double e=0.4090926;
	public static final Rotate EqtoEc = new Rotate('X').setRAngle(-e); 
	
	//Magnitude
	private float Mag;
	
	//B-V Value
	private float B_V;
	
	//Ecliptic pos(Zero time)
	private EVector pos;
	
	public byte[] star_value;
		
	public boolean initialize() {

		if(star_value[103]==' ')
			return false;
		
		double RA, Dec;

		Mag=Spmath.sgnize(star_value[102],
				(float)Spmath.btoi(star_value, 103, 1)
				+Spmath.btoi(star_value, 105, 2)*0.01f);

		B_V=Spmath.sgnize(star_value[109],
				(float)Spmath.btoi(star_value, 110, 1)
				+Spmath.btoi(star_value, 112, 2)*0.01f);

		//J2000
		RA=Spmath.btoi(star_value, 75, 2)*15.0f
				+Spmath.btoi(star_value, 77, 2)/4.0f
				+Spmath.btoi(star_value, 79, 2)/240.0f
				+Spmath.btoi(star_value, 82, 1)/2400.0f;

		Dec=Spmath.sgnize(star_value[83],
				Spmath.btoi(star_value, 84, 2)
				+Spmath.btoi(star_value, 86, 2)/60.0f
				+Spmath.btoi(star_value, 88, 2)/3600.0f);

		pos.set(EqtoEc.transform((IValRef)new SpCoord(RA, Dec).getVec()));

		if(Mag > StellarSky.proxy.getClientSettings().mag_Limit)
			return false;

		star_value=null;
		
		return true;
	}

	@Override
	public IPerDimensionCache createCache() {
		return new BrStarCache();
	}
	
	public class BrStarCache implements IPerDimensionCache {

		private double appMag;
		private double appB_V;
		private EVector appPos;
		
		@Override
		public void update(IStellarViewpoint viewpoint) {
			appPos.set(ExtinctionRefraction.refraction(viewpoint.getProjection(0.0f).transform(pos), true));
			float Airmass=(float) ExtinctionRefraction.airmass(appPos, true);
	    	appMag = (Mag+Airmass*Optics.ext_coeff_V);
	    	appB_V = (B_V+Airmass*Optics.ext_coeff_B_V);
		}
		
	}
}
