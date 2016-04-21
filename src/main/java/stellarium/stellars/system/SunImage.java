package stellarium.stellars.system;

import javax.vecmath.Vector3d;

import stellarapi.api.CelestialPeriod;
import stellarapi.api.ICelestialCoordinate;
import stellarapi.api.ISkyEffect;
import stellarapi.api.celestials.EnumCelestialObjectType;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.optics.Wavelength;
import stellarium.stellars.layer.IPerWorldImage;

public class SunImage implements IPerWorldImage<Sun> {
	
	private double mag;
	private Vector3d pos;
	private SpCoord appCoord = new SpCoord();
	private CelestialPeriod yearPeriod;
	private CelestialPeriod horPeriod;
	
	@Override
	public void initialize(Sun object, ICelestialCoordinate coordinate, ISkyEffect sky,
			CelestialPeriod yearPeriod) {
		this.mag = object.getMagnitude();
		this.yearPeriod = yearPeriod;
		
		this.pos = new Vector3d(object.earthPos);
		CelestialPeriod dayPeriod = coordinate.getPeriod();
		double length = 1 / (1 / dayPeriod.getPeriodLength() - 1 / yearPeriod.getPeriodLength());
		this.horPeriod = new CelestialPeriod("Day", length, coordinate.calculateInitialOffset(this.pos));
	}

	@Override
	public void updateCache(Sun object, ICelestialCoordinate coordinate, ISkyEffect sky) {
		this.pos = new Vector3d(object.earthPos);
		Vector3d ref = new Vector3d(object.earthPos);
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
		return EnumCelestialObjectType.Star;
	}
}