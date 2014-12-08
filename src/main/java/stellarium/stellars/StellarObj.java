package stellarium.stellars;

import sciapi.api.value.IValRef;
import sciapi.api.value.euclidian.EVector;
import sciapi.api.value.euclidian.EVectorSet;
import stellarium.util.math.VecMath;


public abstract class StellarObj {
	
	//Object's Ecliptic Position(Mainly from Sun)
	public EVector EcRPos = new EVector(3);
	
	//Object's Apparent Position
	public EVector AppPos = new EVector(3);
	
	//Magnitude of Object(Except Atmosphere)
	public double Mag;
	
	//Object's Apparent Magnitude
	public double App_Mag;
	
	//Initialize the Object
	abstract public void Initialize();
	
	//Update the Object
	public void Update(){
		AppPos.set(GetAtmPos());
		App_Mag=Mag+ExtinctionRefraction.Airmass(VecMath.getCoord(AppPos, 2).asDouble(), true)*ExtinctionRefraction.ext_coeff_V;
	}
	
	//Get EVectortor of Object from Earth
	abstract public IValRef<EVector> GetPosition();
	
	public IValRef<EVector> GetAtmPos(){
		return ExtinctionRefraction.Refraction(GetPosition(), true);
	}
}
