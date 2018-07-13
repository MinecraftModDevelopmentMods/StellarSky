package stellarium.stellars.util;

import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Spmath;

//Will be corrected
public class ExtinctionRefraction {
	
	static final double subhorizontal_airmass=30.0;
	
	//Calculate Airmass
	@Deprecated
	static double airmass(double cosZ, boolean IsApparent)
	{
		cosZ=Math.abs(cosZ);
		
		double am;
		if (IsApparent)
		{
			// Rozenberg (1966)
			am= (1.0/(cosZ+0.025*Math.exp(-11.0*cosZ)));
		}
		else
		{
			//Young (1994)
			double up=(1.002432*cosZ+0.148386)*cosZ+0.0096467;
			double down=((cosZ+0.149864)*cosZ+0.0102963)*cosZ+0.000303978;
			am=up/down;
		}
		return am;
	}

	//Get Extinction magnitude(in V band) of EVectortor(its size must be 1)
	@Deprecated
	public static double airmass(SpCoord coord, boolean IsApparent){
		return airmass(Math.sin(Math.toRadians(coord.y)), IsApparent);
	}

	@Deprecated
	public static void refraction(SpCoord sp, boolean isApplying) {
		double R;
		
 		if(isApplying)
		{
			//Saemundsson (1986)
 			R=1.02/Math.tan(Math.toRadians(sp.y+10.3/(sp.y+5.11)));
 			sp.y+=R/60.0;
		}
		else
		{
			//Garfinkel (1967)
			R=1.0/Math.tan(Math.toRadians(sp.y+7.31/(sp.y+4.4)));
			sp.y-=R/60.0;
		}
	}
}
