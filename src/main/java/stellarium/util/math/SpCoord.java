package stellarium.util.math;

import sciapi.api.value.IValRef;
import sciapi.api.value.euclidian.EVector;

//Right Ascension(RA) and Declination(Dec)
//-Azimuth and Height
public class SpCoord {
	/**RA or -Azimuth*/
	public double x;
	/**Dec or Height*/
	public double y;
	
	public SpCoord(double a, double b){
		x=a; y=b;
	}
	
	public SpCoord() {
		this(0.0, 0.0);
	}

	/**Gives Vector with this SpCoord*/
	public EVector getVec(){
		EVector ret = new EVector(3);
		
		ret.getVal().getCoord(0).set(Spmath.cosd(y)*Spmath.cosd(x));
		ret.getVal().getCoord(1).set(Spmath.cosd(y)*Spmath.sind(x));
		ret.getVal().getCoord(2).set(Spmath.sind(y));
		
		return ret;
	}
	
	/**Set this SpCoord with vector.
	 * The vector has to be normalized.*/
	public void setWithVec(IValRef<EVector> vec){
		IValRef<EVector> normalized = VecMath.normalize(vec);
		x = Spmath.Degrees(Spmath.atan2(normalized.getVal().getCoord(1).asDouble(), normalized.getVal().getCoord(0).asDouble()));
		y = Spmath.Degrees(Spmath.asin(normalized.getVal().getCoord(2).asDouble()));
	}
}
