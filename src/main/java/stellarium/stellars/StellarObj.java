package stellarium.stellars;

import sciapi.api.value.IValRef;
import sciapi.api.value.euclidian.EVector;
import stellarium.stellars.util.ExtinctionRefraction;
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
	
	//Update the Object
	public void update(){
		appPos.set(getAtmPos());
		appMag=mag+ExtinctionRefraction.airmass(this.appPos, true)*Optics.ext_coeff_V;
	}
	
	//Get EVector of Object from Earth
	abstract public IValRef<EVector> getPosition();
	
	public IValRef<EVector> getAtmPos(){
		return ExtinctionRefraction.refraction(getPosition(), true);
	}
}
