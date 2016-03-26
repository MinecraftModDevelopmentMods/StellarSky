package stellarium.stellars.system;

import sciapi.api.value.IValRef;
import sciapi.api.value.euclidian.CrossUtil;
import sciapi.api.value.euclidian.EVector;
import sciapi.api.value.euclidian.IEVector;
import stellarium.stellars.StellarManager;
import stellarium.util.math.Rotate;
import stellarium.util.math.Spmath;
import stellarium.util.math.VecMath;

public class Moon extends Satellite {
	
	//Additional Orbital Elements for Moon
	protected double a0, e0, I0, w0, Omega0, M0_0;
	protected double wd, Omegad;
	
	protected double brightness;
	
	//Moon's Ecliptic Position Vector from Ground
	EVector posGround = new EVector(3);
	
	
	//Moon's Pole(Ecliptic Coord)
	EVector Pole;
	
	//Moon's Prime Meridian at first
	EVector PrMer0 = new EVector(3);
	
	//Moon's East from Prime Meridian
	EVector East = new EVector(3);
	
	Rotate ri = new Rotate('X'), rom = new Rotate('Z'), rw = new Rotate('Z');


	public Moon(boolean isRemote) {
		super(isRemote);
	}
	
	@Override
	public void initialize() {
		super.initialize();
		Pole=new EVector(0.0, 0.0, 1.0);
		ri.setRAngle(-Spmath.Radians(I0));
		rom.setRAngle(-Spmath.Radians(Omega0));
		Pole.set((IValRef)rom.transform(ri.transform((IEVector)Pole)));
		PrMer0.set(VecMath.normalize(VecMath.mult(-1.0, this.getEcRPosE(0.0))));
		East.set((IValRef)CrossUtil.cross((IEVector)Pole, (IEVector)PrMer0));
	}
	
	//Get Ecliptic Position Vector from Earth
	public IValRef<EVector> getEcRPosE(double yr){
		updateOrbE(yr);
		double M=M0+mean_mot*yr;
		return Spmath.GetOrbVec(a, e, ri.setRAngle(-Spmath.Radians(I)), rw.setRAngle(-Spmath.Radians(w)), rom.setRAngle(-Spmath.Radians(Omega)), M);
	}
	
	//Update Orbital Elements in time
	public void updateOrbE(double yr){
		a=a0;
		e=e0;
		I=I0;
		w=w0+wd*yr;
		Omega=Omega0+Omegad*yr;
		M0=M0_0;
		mean_mot=360.0*Math.sqrt(parPlanet.mass/a)/a;
	}

}
