package stellarium.stellars.system;

import java.util.ArrayList;

import sciapi.api.value.IValRef;
import sciapi.api.value.euclidian.EVector;
import stellarium.stellars.Optics;
import stellarium.stellars.OldSatellite;
import stellarium.stellars.util.ExtinctionRefraction;
import stellarium.util.math.Rotate;
import stellarium.util.math.Spmath;
import stellarium.util.math.VecMath;

public class Planet extends SolarObject {
	
	//Orbital Elements of Planet
	protected double a0, e0, I0, L0, wbar0, Omega0;
	protected double ad, ed, Id, Ld, wbard, Omegad;
	protected double b, c, s, f;
	
	//Satellites
	ArrayList<Satellite> satellites=new ArrayList();

	Rotate roti = new Rotate('X'), rotw = new Rotate('Z'), rotom = new Rotate('Z');
	

	public Planet(boolean isRemote) {
		super(isRemote);
	}
	
	public void addSatellite(Satellite sat){
		sat.parPlanet=this;
		satellites.add(sat);
	}

	@Override
	public EVector getEcRPos(double year) {
		double cen=year/100.0;
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

}
