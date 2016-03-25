package stellarium.stellars.util;

import sciapi.api.value.IValRef;
import sciapi.api.value.euclidian.EVector;
import stellarium.util.math.SpCoord;
import stellarium.util.math.Spmath;
import stellarium.util.math.VecMath;

//Will be corrected
public class ExtinctionRefraction {
	
	static final double subhorizontal_airmass=30.0;
	
	//Calculate Airmass
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
	public static double airmass(IValRef<EVector> vec, boolean IsApparent){
		return airmass(vec.getVal().getCoord(2).asDouble(), IsApparent);
	}
	
	//Get Refraction-applied Vector(IsApplying=true) or Refraction-disapplied Vector(IsApplying=false)
	public static IValRef<EVector> refraction(IValRef<EVector> vec, boolean IsApplying){
				
		double R;
		SpCoord sp = new SpCoord();
		sp.setWithVec(VecMath.normalize(vec));
				
		refraction(sp, IsApplying);
 		
 		return VecMath.mult(VecMath.size(vec), sp.getVec());
	}
	
	public static void refraction(SpCoord sp, boolean isApplying) {
		double R;
		
 		if(isApplying)
		{
			//Saemundsson (1986)
 			R=1.02/Spmath.tand(sp.y+10.3/(sp.y+5.11));
 			sp.y+=R/60.0;
		}
		else
		{
			//Garfinkel (1967)
			R=1.0/Spmath.tand(sp.y+7.31/(sp.y+4.4));
			sp.y-=R/60.0;
		}
	}
}