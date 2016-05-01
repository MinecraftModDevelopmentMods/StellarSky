package stellarium.stellars.system;

import stellarapi.api.lib.math.Matrix3;
import stellarapi.api.lib.math.Spmath;
import stellarapi.api.lib.math.Vector3;
import stellarium.util.math.StellarMath;

public class Planet extends SolarObject {
	
	//Orbital Elements of Planet
	protected double a0, e0, I0, L0, wbar0, Omega0;
	protected double ad, ed, Id, Ld, wbard, Omegad;
	protected double b, c, s, f;
	private double currentOffsetAngle;

	private Matrix3 roti = new Matrix3(), rotw = new Matrix3(), rotom = new Matrix3();
	
	public Planet(String name, SolarObject parent) {
		super(name, parent);
	}

	@Override
	public Vector3 getRelativePos(double year) {
		double cen=year/100.0;
		double a=a0+ad*cen,
				e=e0+ed*cen,
				I=I0+Id*cen,
				L=L0+Ld*cen,
				wbar=wbar0+wbard*cen,
				Omega=Omega0+Omegad*cen;
		double w=wbar-Omega;
		double M = L-wbar+b*cen*cen+c*Spmath.cosd(f*cen)+s*Spmath.sind(f*cen);
		this.currentOffsetAngle = M + w;
		
		roti.setAsRotation(1.0, 0.0, 0.0, -Spmath.Radians(I));
		rotw.setAsRotation(0.0, 0.0, 1.0, -Spmath.Radians(w));
		rotom.setAsRotation(0.0, 0.0, 1.0, -Spmath.Radians(Omega));

		Matrix3 matrix = new Matrix3();
		matrix.setIdentity();
		matrix.postMult(this.rotom);
		matrix.postMult(this.roti);
		matrix.postMult(this.rotw);
		
		return StellarMath.GetOrbVec(a, e, M, matrix);
	}
	
	@Override
	public double absoluteOffset() {
		return this.currentOffsetAngle / 360.0;
	}
	
	public double getRevolutionPeriod() {
		return 36000.0 / this.Ld;
	}

}
