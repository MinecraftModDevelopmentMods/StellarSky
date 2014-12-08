package stellarium.stellars;

import sciapi.api.value.IValRef;
import sciapi.api.value.euclidian.EVector;
import stellarium.util.math.Rotate;
import stellarium.util.math.Spmath;
import stellarium.util.math.VecMath;

public class Satellite extends SolarObj {
	
	Planet Parplanet;
	
	//Orbital Elements
	double a, e, I, w, Omega, M0;
	double mean_mot;
	
	Rotate ri = new Rotate('X'), rw = new Rotate('Z'), rom = new Rotate('Z');

	//Get Satellite's Ecliptic Position of 
	@Override
	public IValRef<EVector> GetEcRPos(double time) {
		double M=M0+mean_mot*time;
		ri.setRAngle(-I);
		rw.setRAngle(-w);
		rom.setRAngle(-Omega);
		return VecMath.add(Spmath.GetOrbVec(a, e, ri, rw, rom, M), Parplanet.EcRPos);
	}
	
	//Update Satellite
	public void Update(){
		super.Update();
	}
	
	
	//Initialize
	@Override
	public void Initialize() {
		mean_mot=Parplanet.Mass;
	}
}
