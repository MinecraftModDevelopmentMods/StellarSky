package stellarium.stellars.system;

import stellarapi.api.CelestialPeriod;
import stellarapi.api.ICelestialCoordinate;
import stellarapi.api.ISkyEffect;
import stellarapi.api.celestials.EnumCelestialObjectType;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Vector3;
import stellarapi.api.optics.Wavelength;
import stellarium.stellars.layer.IPerWorldImage;

public class SunImage implements IPerWorldImage<Sun> {
	
	private double mag;
	private Vector3 pos;
	protected SpCoord appCoord = new SpCoord();
	private CelestialPeriod yearPeriod;
	private CelestialPeriod horPeriod;
	
	@Override
	public void initialize(Sun object, ICelestialCoordinate coordinate, ISkyEffect sky,
			CelestialPeriod yearPeriod) {
		this.mag = object.getMagnitude();
		this.yearPeriod = new CelestialPeriod("Year", yearPeriod.getPeriodLength(), object.absoluteOffset());
		
		this.pos = new Vector3(object.earthPos);
		CelestialPeriod dayPeriod = coordinate.getPeriod();
		double length = 1 / (1 / dayPeriod.getPeriodLength() - 1 / yearPeriod.getPeriodLength());
		this.horPeriod = new CelestialPeriod("Day", length, coordinate.calculateInitialOffset(this.pos, length));
	}

	@Override
	public void updateCache(Sun object, ICelestialCoordinate coordinate, ISkyEffect sky) {
		this.pos = new Vector3(object.earthPos);
		Vector3 ref = new Vector3(object.earthPos);
		coordinate.getProjectionToGround().transform(ref);
		appCoord.setWithVec(ref);
		sky.applyAtmRefraction(this.appCoord);
	}

	@Override
	public CelestialPeriod getAbsolutePeriod() {
		return this.yearPeriod;
	}

	@Override
	public CelestialPeriod getHorizontalPeriod() {
		return this.horPeriod;
	}

	@Override
	public CelestialPeriod getPhasePeriod() {
		return null;
	}

	@Override
	public double getCurrentPhase() {
		return 1.0;
	}

	@Override
	public double getCurrentBrightness(Wavelength wavelength) {
		return 1.0;
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
		return EnumCelestialObjectType.Star;
	}

	@Override
	public String getName() {
		return "Sun";
	}
}