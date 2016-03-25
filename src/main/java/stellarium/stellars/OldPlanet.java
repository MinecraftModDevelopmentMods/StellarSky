package stellarium.stellars;

import java.util.ArrayList;

import sciapi.api.value.IValRef;
import sciapi.api.value.euclidian.EVector;
import sciapi.api.value.util.BOp;
import sciapi.api.value.util.VOp;
import stellarium.StellarSky;
import stellarium.stellars.util.ExtinctionRefraction;
import stellarium.util.math.Rotate;
import stellarium.util.math.Spmath;
import stellarium.util.math.VecMath;

public class OldPlanet extends SolarObj{
	
	OldPlanet(){
		b=c=s=f=0.0;
	}
	
	//Orbital Elements of Planet
	double a0, e0, I0, L0, wbar0, Omega0;
	double ad, ed, Id, Ld, wbard, Omegad;
	double b, c, s, f;
	
	//Planet's Pole(Ecliptic Coord)
	//EVector pole;
	
	//Planet's Prime Meridian at first
	//EVector prMer0;
	
	//Planet's East from Prime Meridian
	//EVector east;
	
	//Rotating angular velocity
	double rot;
	
	//Mass of Planet
	double mass;
	
	//Satellites
	ArrayList<Satellite> satellites=new ArrayList(1);
	
	//Planet name
	char name[];

	Rotate roti = new Rotate('X'), rotw = new Rotate('Z'), rotom = new Rotate('Z');
	
	@Override
	//Calculate Planet's Ecliptic EVectortor from Sun
	public IValRef<EVector> getEcRPos(double yr) {
		double cen=yr/100.0;
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
	/*public IValRef<EVector> posLocalP(double longitude, double latitude, double time){
		double longp=Spmath.Radians(longitude+rot*time);
		double lat=Spmath.Radians(latitude);
		return VOp.mult(radius, BOp.add(BOp.add(VecMath.mult(Math.sin(lat), pole), VecMath.mult(Math.cos(lat)*Math.cos(longp), prMer0)), VecMath.mult(Math.cos(lat)*Math.sin(longp), east)));
	}*/
	
	//Ecliptic Position of Planet's Local Region from Earth (Update Needed)
	/*public IValRef<EVector> posLocalE(double longitude, double latitude, double time){
		return VecMath.add(EcRPosE, posLocalP(longitude, latitude, time));
	}*/
	
	//Update magnitude
	public void updateMagnitude(){
		double dist=Spmath.getD(VecMath.size(EcRPosE));
		double distS=Spmath.getD(VecMath.size(EcRPos));
		double distE=Spmath.getD(VecMath.size(getManager().Earth.EcRPos));
		double LvsSun=this.radius.asDouble()*this.radius.asDouble()*this.getPhase()*distE*distE*albedo*1.4/(dist*dist*distS*distS);
		this.mag=-26.74-2.5*Math.log10(LvsSun);
	}
	


	//Update Planet
	@Override
	public void update() {
		EcRPos.set(getEcRPos(getManager().transforms.yr));
		EcRPosE.set(VecMath.sub(this.EcRPos, getManager().Earth.EcRPos));
		
		for(int i=0; i<satellites.size(); i++)
			satellites.get(i).update();
		
		this.updateMagnitude();
		
		appPos.set(getAtmPos());
		appMag=mag+ExtinctionRefraction.airmass(appPos, true)*Optics.ext_coeff_V;
	}
	
	public void addSatellite(Satellite sat){
		sat.parPlanet=this;
		satellites.add(sat);
	}


	@Override
	public void initialize(StellarManager manager) {
//		East=EVector.Cross(Pole, PrMer0);
		super.initialize(manager);
		for(int i=0; i<satellites.size(); i++)
			satellites.get(i).initialize(manager);
	}

}