package stellarium.stellars.system;

import stellarapi.api.CelestialPeriod;
import stellarapi.api.ICelestialCoordinate;
import stellarapi.api.ISkyEffect;
import stellarapi.api.celestials.EnumCelestialObjectType;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Vector3;
import stellarapi.api.optics.Wavelength;
import stellarium.stellars.layer.IPerWorldImage;

public class PlanetImage implements IPerWorldImage<Planet> {

	private double mag;
	private Vector3 pos;
	private SpCoord appCoord = new SpCoord();
	private double phase;
	private CelestialPeriod siderealPeriod, synodicPeriod;
	private CelestialPeriod horPeriod;

	@Override
	public void initialize(Planet object, ICelestialCoordinate coordinate, ISkyEffect sky, CelestialPeriod yearPeriod) {
		double LvsSun=object.radius*object.radius*object.albedo*1.4/(object.a0*object.a0*object.a0*object.a0);
		this.mag=-26.74-2.5*Math.log10(LvsSun);
		
		double period = object.getRevolutionPeriod() * yearPeriod.getPeriodLength();
		this.siderealPeriod = new CelestialPeriod(String.format("Sidereal Period of %s", object.getID()), period, object.absoluteOffset());
		this.synodicPeriod = new CelestialPeriod(String.format("Synodic Period of %s", object.getID()), 1/(1/period - 1/yearPeriod.getPeriodLength()),
				object.phaseOffset());
		
		this.pos = new Vector3(object.earthPos);
		CelestialPeriod dayPeriod = coordinate.getPeriod();
		double length = 1 / (1 / dayPeriod.getPeriodLength() - 1 / synodicPeriod.getPeriodLength());
		this.horPeriod = new CelestialPeriod(String.format("Day for %s", object.getID()), length, coordinate.calculateInitialOffset(this.pos, length));
	}

	@Override
	public void updateCache(Planet object, ICelestialCoordinate coordinate, ISkyEffect sky) {
		this.pos = new Vector3(object.earthPos);
		Vector3 ref = new Vector3(object.earthPos);
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
	public Vector3 getCurrentAbsolutePos() {
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
