package stellarium.stellars;

import sciapi.api.value.IValRef;
import sciapi.api.value.euclidian.EVector;
import stellarium.util.math.SpCoord;
import stellarium.util.math.SpCoordf;
import stellarium.util.math.Spmath;
import stellarium.util.math.Transforms;
import stellarium.util.math.VecMath;

//Will be corrected
public class ExtinctionRefraction {
	
	static final double subhorizontal_airmass=30.0;
	
	public static final double ext_coeff_V=0.2;
	public static final double ext_coeff_B_V=0.1;
	
	public static final float ext_coeff_Vf=0.2f;
	public static final float ext_coeff_B_Vf=0.1f;

	//Calculate Airmass
	static double Airmass(double cosZ, boolean IsApparent)
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
	public static double Airmass(IValRef<EVector> vec, boolean IsApparent){
		return Airmass(vec.getVal().getCoord(2).asDouble(), IsApparent);
	}
	
	//Calculate Airmass
	static float Airmass(float cosZ, boolean IsApparent)
	{
		cosZ=Math.abs(cosZ);
		
		float am;
		if (IsApparent)
		{
			// Rozenberg (1966)
			am= (1.0f/(cosZ+0.025f*(float)Math.exp(-11.0f*cosZ)));
		}
		else
		{
			//Young (1994)
			float up=(1.002432f*cosZ+0.148386f)*cosZ+0.0096467f;
			float down=((cosZ+0.149864f)*cosZ+0.0102963f)*cosZ+0.000303978f;
			am=up/down;
		}
		return am;
	}
	
	//Get Refraction-applied Vector(IsApplying=true) or Refraction-disapplied Vector(IsApplying=false)
	public static IValRef<EVector> Refraction(IValRef<EVector> vec, boolean IsApplying){
				
		double R;
		SpCoord sp = new SpCoord();
		sp.setWithVec(VecMath.normalize(vec));
				
 		if(IsApplying)
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
 		
 		return VecMath.mult(VecMath.size(vec), sp.getVec());
	}
}
