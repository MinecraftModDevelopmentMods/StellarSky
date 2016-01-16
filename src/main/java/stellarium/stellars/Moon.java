package stellarium.stellars;

import sciapi.api.value.IValRef;
import sciapi.api.value.euclidian.CrossUtil;
import sciapi.api.value.euclidian.EVector;
import sciapi.api.value.euclidian.IEVector;
import sciapi.api.value.numerics.DDouble;
import sciapi.api.value.numerics.IReal;
import sciapi.api.value.util.BOp;
import sciapi.api.value.util.VOp;
import stellarium.StellarSky;
import stellarium.util.math.Rotate;
import stellarium.util.math.Spmath;
import stellarium.util.math.Transforms;
import stellarium.util.math.VecMath;

public class Moon extends Satellite {
		
	//Additional Orbital Elements for Moon
	double a0, e0, I0, w0, Omega0, M0_0;
	double wd, Omegad;

	double brightness;
	
	//Moon's Ecliptic Position Vector from Ground
	EVector EcRPosG = new EVector(3);
	
	
	//Moon's Pole(Ecliptic Coord)
	EVector Pole;
	
	//Moon's Prime Meridian at first
	EVector PrMer0 = new EVector(3);
	
	//Moon's East from Prime Meridian
	EVector East = new EVector(3);
	
	Rotate ri = new Rotate('X'), rom = new Rotate('Z'), rw = new Rotate('Z');

	
	public void initialize(){
		Pole=new EVector(0.0, 0.0, 1.0);
		ri.setRAngle(-Spmath.Radians(I0));
		rom.setRAngle(-Spmath.Radians(Omega0));
		Pole.set((IValRef)rom.transform(ri.transform((IEVector)Pole)));
		PrMer0.set(VecMath.normalize(VecMath.mult(-1.0, this.GetEcRPosE(0.0))));
		East.set((IValRef)CrossUtil.cross((IEVector)Pole, (IEVector)PrMer0));
	}
	
	//Get Ecliptic Position Vector from Earth
	public IValRef<EVector> GetEcRPosE(double yr){
		UpdateOrbE(yr);
		double M=M0+mean_mot*yr;
		return Spmath.GetOrbVec(a, e, ri.setRAngle(-Spmath.Radians(I)), rw.setRAngle(-Spmath.Radians(w)), rom.setRAngle(-Spmath.Radians(Omega)), M);
	}
	
	//Update Orbital Elements in time
	public void UpdateOrbE(double yr){
		a=a0;
		e=e0;
		I=I0;
		w=w0+wd*yr;
		Omega=Omega0+Omegad*yr;
		M0=M0_0;
		mean_mot=360.0*Math.sqrt(parPlanet.mass/a)/a;
	}

	
	//Update Moon(Use After Earth is Updated)
	public void update(){
		double yr=Transforms.yr;
		
		EcRPosE.set(GetEcRPosE(yr));
		EcRPos.set(VecMath.add(parPlanet.getEcRPos(yr),EcRPosE));
		EcRPosG.set(VecMath.sub(EcRPosE,Transforms.Zen));
		
		appPos.set(getAtmPos());
		/*App_Mag=Mag+ExtinctionRefraction.Airmass(AppPos.z, true)*ExtinctionRefraction.ext_coeff_V;*/
		this.updateMagnitude();
	}
	
	//Update magnitude and brightness
	public void updateMagnitude(){
		double dist=Spmath.getD(VecMath.size(EcRPosG));
		double distS=Spmath.getD(VecMath.size(EcRPos));
		double distE=Spmath.getD(VecMath.size(StellarSky.getManager().Earth.EcRPos));
		double LvsSun=this.radius.asDouble()*this.radius.asDouble()*this.getPhase()*distE*distE*albedo*1.4/(dist*dist*distS*distS);
		this.mag=-26.74-2.5*Math.log10(LvsSun);
		
		this.brightness = distE*distE*this.albedo/(distS*distS)*10;
	}
	
	public IValRef<EVector> getPosition(){
		IValRef pvec=Transforms.ZTEctoNEc.transform((IValRef)EcRPosG);
		pvec=Transforms.EctoEq.transform(pvec);
		pvec=Transforms.NEqtoREq.transform(pvec);
		pvec=Transforms.REqtoHor.transform(pvec);
		return pvec;
	}
	
	//Ecliptic Position of Moon's Local Region from Moon Center (Update Needed)
	public synchronized IValRef<EVector> posLocalM(double longitude, double lattitude, double yr){
		float longp=(float)Spmath.Radians(longitude+mean_mot*yr);
		float lat=(float)Spmath.Radians(lattitude);
		return VecMath.mult((IValRef)radius, VecMath.add(VecMath.add(VecMath.mult(Spmath.sinf(lat), Pole), VecMath.mult(Spmath.cosf(lat)*Spmath.cosf(longp), PrMer0)), VecMath.mult(Spmath.cosf(lat)*Spmath.sinf(longp), East)));
	}
	
	//Ecliptic Position of Moon's Local Region from Ground (Update Needed)
	//Parameter: PosLocalM Result
	public synchronized IValRef<EVector> posLocalG(IValRef<EVector> p){
		return VecMath.add(EcRPosG, p);
	}
	
	
	//Illumination of Moon's Local Region (Update Needed)
	//Parameter: PosLocalM Result
	public double illumination(EVector p){
		return -Spmath.getD(BOp.div(VecMath.dot(EcRPos, p), BOp.mult(VecMath.size(EcRPos), VecMath.size(p))))*brightness;
	}
	
	//Phase of the Moon(Update Needed)
	public double getPhase(){
		return 1-(Math.PI-Math.acos(Spmath.getD(BOp.div(VecMath.dot(EcRPos, EcRPosG), BOp.mult(VecMath.size(EcRPos), VecMath.size(EcRPosG))))))/Math.PI;
	}
	
	//Time phase for moon
	public double phase_Time(){
		double k=Math.signum(Spmath.getD(VOp.dot(CrossUtil.cross((IValRef)EcRPosG, (IValRef)EcRPos), (IValRef)Pole)))*getPhase();
		if(k<0) k=k+2;
		return k/2;
	}
}
