package stellarium.stellars.system;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

import stellarapi.api.lib.math.Spmath;
import stellarium.render.IRenderCache;
import stellarium.util.math.StellarMath;

public class Planet extends SolarObject {
	
	//Orbital Elements of Planet
	protected double a0, e0, I0, L0, wbar0, Omega0;
	protected double ad, ed, Id, Ld, wbard, Omegad;
	protected double b, c, s, f;

	private Matrix3d roti = new Matrix3d(), rotw = new Matrix3d(), rotom = new Matrix3d();
	
	public Planet(boolean isRemote, SolarObject parent) {
		super(isRemote, parent);
	}
	
	@Override
	public IRenderCache generateCache() {
		return new PlanetRenderCache();
	}

	@Override
	public Vector3d getRelativePos(double year) {
		double cen=year/100.0;
		double a=a0+ad*cen,
				e=e0+ed*cen,
				I=I0+Id*cen,
				L=L0+Ld*cen,
				wbar=wbar0+wbard*cen,
				Omega=Omega0+Omegad*cen;
		double w=wbar-Omega;
		double M=L-wbar+b*cen*cen+c*Spmath.cosd(f*cen)+s*Spmath.sind(f*cen);
		
		roti.set(new AxisAngle4d(1.0, 0.0, 0.0, -Spmath.Radians(I)));
		rotw.set(new AxisAngle4d(0.0, 0.0, 1.0, -Spmath.Radians(w)));
		rotom.set(new AxisAngle4d(0.0, 0.0, 1.0, -Spmath.Radians(Omega)));

		Matrix3d matrix = new Matrix3d();
		matrix.setIdentity();
		matrix.mul(this.rotom);
		matrix.mul(this.roti);
		matrix.mul(this.rotw);
		
		return StellarMath.GetOrbVec(a, e, M, matrix);
	}

	@Override
	public int getRenderId() {
		return LayerSolarSystem.planetRenderId;
	}

}
