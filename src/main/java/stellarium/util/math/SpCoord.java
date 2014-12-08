package stellarium.util.math;

import sciapi.api.value.IValRef;
import sciapi.api.value.STempRef;
import sciapi.api.value.euclidian.EVector;
import sciapi.api.value.euclidian.EVectorSet;

//Right Ascension(RA) and Declination(Dec)
//-Azimuth and Height
public class SpCoord {
	//RA or -Azimuth
	public double x;
	//Dec or Height
	public double y;
	
	public SpCoord(double a, double b){
		x=a; y=b;
	}
	
	public SpCoord() {
		this(0.0, 0.0);
	}

	/**Gives Vector with this SpCoord*/
	public IValRef<EVector> getVec(){
		STempRef<EVector> ret = EVectorSet.ins(3).getSTemp();
		
		ret.getVal().getCoord(0).set(Spmath.cosd(y)*Spmath.cosd(x));
		ret.getVal().getCoord(1).set(Spmath.cosd(y)*Spmath.sind(x));
		ret.getVal().getCoord(2).set(Spmath.sind(y));
		
		return ret;
	}
	
	/**Set this SpCoord with vector.
	 * The vector has to be normalized.*/
	public void setWithVec(IValRef<EVector> vec){
		x = Spmath.Degrees(Math.atan2(vec.getVal().getCoord(1).asDouble(), vec.getVal().getCoord(0).asDouble()));
		y = Spmath.Degrees(Math.asin(vec.getVal().getCoord(2).asDouble()));
		
		vec.onUsed();
	}
}
