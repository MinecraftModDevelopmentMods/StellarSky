package stellarium.stellars.system;

import sciapi.api.value.euclidian.EVector;
import stellarium.stellars.layer.IRenderCache;
import stellarium.util.math.Rotate;
import stellarium.util.math.Spmath;

public class Planet extends SolarObject {
	
	//Orbital Elements of Planet
	protected double a0, e0, I0, L0, wbar0, Omega0;
	protected double ad, ed, Id, Ld, wbard, Omegad;
	protected double b, c, s, f;

	private Rotate roti = new Rotate('X'), rotw = new Rotate('Z'), rotom = new Rotate('Z');
	
	public Planet(boolean isRemote, SolarObject parent) {
		super(isRemote, parent);
	}
	
	@Override
	public IRenderCache generateCache() {
		return new PlanetRenderCache();
	}

	@Override
	public EVector getRelativePos(double year) {
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

	@Override
	public int getRenderId() {
		return LayerSolarSystem.planetRenderId;
	}

}
