package stellarium.util.math;

import sciapi.api.value.IValRef;
import sciapi.api.value.STempRef;
import sciapi.api.value.euclidian.EVector;
import sciapi.api.value.euclidian.EVectorSet;

public class SpCoordf {
	//RA or -Azimuth
	public float x;
	//Dec or Height
	public float y;
	
	public SpCoordf(float a, float b){
		x=a; y=b;
	}
	
	//Get Vector from SpCoord
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
		x = (float) Spmath.Degrees(Math.atan2(vec.getVal().getCoord(1).asFloat(), vec.getVal().getCoord(0).asFloat()));
		y = (float) Spmath.Degrees(Math.asin(vec.getVal().getCoord(2).asFloat()));
		
		vec.onUsed();
	}
}
