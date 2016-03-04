package stellarium.stellars;

import sciapi.api.value.IValRef;
import sciapi.api.value.euclidian.EVector;
import sciapi.api.value.euclidian.EVectorSet;
import stellarium.util.math.VecMath;


public abstract class StellarObj {
	
	//Object's Ecliptic Position(Mainly from Sun)
	public EVector EcRPos = new EVector(3);
	
	//Object's Apparent Position
	public EVector appPos = new EVector(3);
	
	//Magnitude of Object(Except Atmosphere)
	public double mag;
	
	//Object's Apparent Magnitude
	public double appMag;
	
	//Initialize the Object
	abstract public void initialize();
	
	//Update the Object
	public void update(){
		appPos.set(getAtmPos());
		appMag=mag+ExtinctionRefraction.airmass(VecMath.getCoord(appPos, 2).asDouble(), true)*ExtinctionRefraction.ext_coeff_V;
	}
	
	//Get EVector of Object from Earth
	abstract public IValRef<EVector> getPosition();
	
	public IValRef<EVector> getAtmPos(){
		return ExtinctionRefraction.refraction(getPosition(), true);
	}
}
