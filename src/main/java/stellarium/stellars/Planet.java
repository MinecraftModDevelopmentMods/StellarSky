package stellarium.stellars;

import java.util.ArrayList;

import sciapi.api.value.IValRef;
import sciapi.api.value.euclidian.EVector;
import sciapi.api.value.util.BOp;
import sciapi.api.value.util.VOp;
import stellarium.util.math.Rotate;
import stellarium.util.math.Spmath;
import stellarium.util.math.Transforms;
import stellarium.util.math.VecMath;

public class Planet extends SolarObj{
	
	Planet(){
		b=c=s=f=0.0;
	}
	
	//Orbital Elements of Planet
	double a0, e0, I0, L0, wbar0, Omega0;
	double ad, ed, Id, Ld, wbard, Omegad;
	double b, c, s, f;
	
	//Planet's Pole(Ecliptic Coord)
	EVector Pole;
	
	//Planet's Prime Meridian at first
	EVector PrMer0;
	
	//Planet's East from Prime Meridian
	EVector East;
	
	//Rotating angular velocity
	double Rot;
	
	//Mass of Planet
	double Mass;
	
	//Albedo of Planet
	double Albedo;
	
	//Satellites
	ArrayList<Satellite> satellites=new ArrayList(1);
	
	//Planet name
	char name[];

	Rotate roti = new Rotate('X'), rotw = new Rotate('Z'), rotom = new Rotate('Z');
	
	@Override
	//Calculate Planet's Ecliptic EVectortor from Sun
	public IValRef<EVector> GetEcRPos(double time) {
		double day=time/24000.0;
		double cen=day/36525.0;
		double a=a0+ad*cen,
				e=e0+ed*cen,
				I=I0+Id*cen,
				L=L0+Ld*cen,
				wbar=wbar0+wbard*cen,
				Omega=Omega0+Omegad*cen;
		double w=wbar-Omega;
		double M=L-wbar+b*cen*cen+c*Spmath.cosd(f*cen)+s*Spmath.sind(f*cen);
		
		roti.setRAngle(-Spmath.Radians(I));
		rotw.setRAngle(-Spmath.Radians(w));
		rotom.setRAngle(-Spmath.Radians(Omega));
		
		return Spmath.GetOrbVec(a, e, roti, rotw, rotom, M);
	}
	
	//Ecliptic Position of Planet's Local Region from Moon Center (Update Needed)
	public IValRef<EVector> PosLocalP(double longitude, double lattitude, double time){
		double longp=Spmath.Radians(longitude+Rot*time);
		double lat=Spmath.Radians(lattitude);
		return VOp.mult(Radius, BOp.add(BOp.add(VecMath.mult(Math.sin(lat), Pole), VecMath.mult(Math.cos(lat)*Math.cos(longp), PrMer0)), VecMath.mult(Math.cos(lat)*Math.sin(longp), East)));
	}
	
	//Ecliptic Position of Planet's Local Region from Earth (Update Needed)
	public IValRef<EVector> PosLocalE(double longitude, double lattitude, double time){
		return VecMath.add(EcRPosE, PosLocalP(longitude, lattitude, time));
	}
	
	//Update magnitude
	public void UpdateMagnitude(){
		double dist=Spmath.getD(VecMath.size(EcRPosE));
		double distS=Spmath.getD(VecMath.size(EcRPos));
		double distE=Spmath.getD(VecMath.size(StellarManager.Earth.EcRPos));
		double LvsSun=this.Radius.asDouble()*this.Radius.asDouble()*this.GetPhase()*distE*distE*Albedo*1.4/(dist*dist*distS*distS);
		this.Mag=-26.74-2.5*Math.log10(LvsSun);
	}
	


	//Update Planet
	@Override
	public void Update() {
		EcRPos.set(GetEcRPos(Transforms.time));
		EcRPosE.set(VecMath.sub(this.EcRPos, StellarManager.Earth.EcRPos));
		
		for(int i=0; i<satellites.size(); i++)
			satellites.get(i).Update();
		
		this.UpdateMagnitude();
		
		AppPos.set(GetAtmPos());
		App_Mag=Mag+ExtinctionRefraction.Airmass(AppPos, true)*ExtinctionRefraction.ext_coeff_V;
	}
	
	public void AddSatellite(Satellite sat){
		sat.Parplanet=this;
		satellites.add(sat);
	}


	@Override
	public void Initialize() {
//		East=EVector.Cross(Pole, PrMer0);
		for(int i=0; i<satellites.size(); i++)
			satellites.get(i).Initialize();
	}

}
