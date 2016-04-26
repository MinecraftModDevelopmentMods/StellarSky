package stellarium.stellars.system;

import javax.vecmath.Vector3d;

import stellarapi.api.CelestialPeriod;
import stellarapi.api.ICelestialCoordinate;
import stellarapi.api.ISkyEffect;
import stellarapi.api.celestials.EnumCelestialObjectType;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.optics.Wavelength;
import stellarium.stellars.layer.IPerWorldImage;

public class MoonImage implements IPerWorldImage<Moon> {

	private double mag;
	private Vector3d pos;
	private SpCoord appCoord = new SpCoord();
	private double phase;
	private CelestialPeriod siderealPeriod, synodicPeriod;
	private CelestialPeriod horPeriod;

	@Override
	public void initialize(Moon object, ICelestialCoordinate coordinate, ISkyEffect sky, CelestialPeriod yearPeriod) {
		this.mag = -12.74 - 2.5 * Math.log(object.brightnessFactor);
		
		this.pos = new Vector3d(object.earthPos);
		
		double period = object.getRevolutionPeriod();
		
		this.siderealPeriod = new CelestialPeriod("Sidereal Lunar Month", period, object.M0_0 / 360.0);
		this.synodicPeriod = new CelestialPeriod("Lunar Month", 1/(1/period - 1/yearPeriod.getPeriodLength()),
				object.phase_Time());
		
		CelestialPeriod dayPeriod = coordinate.getPeriod();
		double length = 1 / (1 / dayPeriod.getPeriodLength() - 1 / synodicPeriod.getPeriodLength());
		this.horPeriod = new CelestialPeriod("Lunar Day", length, coordinate.calculateInitialOffset(this.pos, length));
	}

	@Override
	public void updateCache(Moon object, ICelestialCoordinate coordinate, ISkyEffect sky) {
		this.pos = new Vector3d(object.earthPos);
		Vector3d ref = new Vector3d(object.earthPos);
		coordinate.getProjectionToGround().transform(ref);
		appCoord.setWithVec(ref);
		sky.applyAtmRefraction(this.appCoord);
		this.phase = object.getPhase();
	}
	
	@Override
	public CelestialPeriod getAbsolutePeriod() {
		return this.siderealPeriod;
	}

	@Override
	public CelestialPeriod getHorizontalPeriod() {
		return this.horPeriod;
	}

	@Override
	public CelestialPeriod getPhasePeriod() {
		return this.synodicPeriod;
	}

	@Override
	public double getCurrentPhase() {
		return this.phase;
	}

	@Override
	public double getCurrentBrightness(Wavelength wavelength) {
		return this.phase;
	}

	@Override
	public Vector3d getCurrentAbsolutePos() {
		return this.pos;
	}

	@Override
	public SpCoord getCurrentHorizontalPos() {
		return this.appCoord;
	}

	@Override
	public double getStandardMagnitude() {
		return this.mag;
	}

	@Override
	public EnumCelestialObjectType getObjectType() {
		return EnumCelestialObjectType.Planet;
	}

}
