package stellarium.stellars.system;

import sciapi.api.value.euclidian.EVector;
import stellarium.util.math.Rotate;
import stellarium.util.math.Spmath;
import stellarium.util.math.VecMath;

public class Satellite extends SolarObject {
	
	protected Planet parPlanet;
	
	//Orbital Elements
	protected double a, e, I, w, Omega, M0;
	protected double mean_mot;
	
	Rotate ri = new Rotate('X'), rw = new Rotate('Z'), rom = new Rotate('Z');

	public Satellite(boolean isRemote) {
		super(isRemote);
	}

	@Override
	public EVector getEcRPos(double year) {
		double M=M0+mean_mot*year;
		ri.setRAngle(-I);
		rw.setRAngle(-w);
		rom.setRAngle(-Omega);
		return VecMath.add(Spmath.GetOrbVec(a, e, ri, rw, rom, M), parPlanet.EcRPos);
	}
	
	public void initialize() {
		super.initialize();
		mean_mot=parPlanet.mass;
	}

}
