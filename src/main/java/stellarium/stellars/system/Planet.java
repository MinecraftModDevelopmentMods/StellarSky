package stellarium.stellars.system;

import stellarapi.api.CelestialPeriod;
import stellarapi.api.celestials.EnumObjectType;
import stellarapi.api.lib.math.Matrix3;
import stellarapi.api.lib.math.Vector3;
import stellarapi.api.optics.Wavelength;
import stellarapi.api.view.ICCoordinates;
import stellarium.util.math.StellarMath;

// TODO Fix this mess on solar objects
public class Planet extends SolarObject {

	//Orbital Elements of Planet
	protected double a0, e0, I0, L0, wbar0, Omega0;
	protected double ad, ed, Id, Ld, wbard, Omegad;
	protected double b, c, s, f;
	private double currentOffsetAngle;

	private Matrix3 roti = new Matrix3(), rotw = new Matrix3(), rotom = new Matrix3();

	public Planet(String name, SolarObject parent) {
		super(name, parent, EnumObjectType.Planet);
		double LvsSun=this.radius*this.radius*this.albedo*1.4/(this.a0*this.a0*this.a0*this.a0);
		this.setStandardMagnitude(-26.74-2.5*Math.log10(LvsSun));
	}

	@Override
	public void setupCoord(ICCoordinates coords, CelestialPeriod yearPeriod) {
		double period = this.getRevolutionPeriod() * yearPeriod.getPeriodLength();
		this.setAbsolutePeriod(new CelestialPeriod(String.format("Sidereal Period of %s", this.getName().getResourcePath()), period, this.absoluteOffset()));
		CelestialPeriod synodicPeriod = new CelestialPeriod(String.format("Synodic Period of %s", this.getName().getResourcePath()), 1/(1/period - 1/yearPeriod.getPeriodLength()),
				this.phaseOffset());
		this.setPhasePeriod(synodicPeriod);

		CelestialPeriod dayPeriod = coords.getPeriod();
		double length = 1 / (1 / dayPeriod.getPeriodLength() - 1 / synodicPeriod.getPeriodLength());
		this.setHoritontalPeriod(new CelestialPeriod(String.format("Day for %s", this.getName().getResourcePath()), length, coords.calculateInitialOffset(this.earthPos, length)));
	}

	@Override
	public double getCurrentBrightness(Wavelength wavelength) {
		// TODO Fix this
		return this.getCurrentPhase();
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
		double M = L-wbar+b*cen*cen+c*Math.cos(Math.toRadians(f*cen))+
				s*Math.sin(Math.toRadians(f*cen));
		this.currentOffsetAngle = M + w;

		roti.setAsRotation(1.0, 0.0, 0.0, -Math.toRadians(I));
		rotw.setAsRotation(0.0, 0.0, 1.0, -Math.toRadians(w));
		rotom.setAsRotation(0.0, 0.0, 1.0, -Math.toRadians(Omega));

		Matrix3 matrix = new Matrix3();
		matrix.setIdentity();
		matrix.postMult(this.rotom);
		matrix.postMult(this.roti);
		matrix.postMult(this.rotw);

		return StellarMath.getOrbVec(a, e, M, matrix);
	}

	@Override
	public double absoluteOffset() {
		return this.currentOffsetAngle / 360.0;
	}

	public double getRevolutionPeriod() {
		return 36000.0 / this.Ld;
	}

}
