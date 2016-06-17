package stellarium.stellars.deepsky;

import stellarapi.api.CelestialPeriod;
import stellarapi.api.ICelestialCoordinate;
import stellarapi.api.ISkyEffect;
import stellarapi.api.celestials.EnumCelestialObjectType;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Vector3;
import stellarapi.api.optics.Wavelength;
import stellarium.stellars.layer.IPerWorldImage;

public class DeepSkyImage implements IPerWorldImage<DeepSkyObject> {

	private DeepSkyObject object;
	private CelestialPeriod horizontalPeriod;
	protected SpCoord appPos = new SpCoord();
	protected double radius;
	
	@Override
	public void initialize(DeepSkyObject object, ICelestialCoordinate coordinate, ISkyEffect sky,
			CelestialPeriod yearPeriod) {
		this.object = object;
		this.horizontalPeriod = new CelestialPeriod(String.format("Day; for %s", object.name),
				coordinate.getPeriod().getPeriodLength(),
				coordinate.calculateInitialOffset(object.centerPos, coordinate.getPeriod().getPeriodLength()));
		this.radius = object.getRadius();
	}

	@Override
	public void updateCache(DeepSkyObject object, ICelestialCoordinate coordinate, ISkyEffect sky) {
		Vector3 ref = new Vector3(object.centerPos);
		coordinate.getProjectionToGround().transform(ref);
		appPos.setWithVec(ref);
		sky.applyAtmRefraction(this.appPos);
	}

	@Override
	public CelestialPeriod getAbsolutePeriod() {
		return null;
	}

	@Override
	public Vector3 getCurrentAbsolutePos() {
		return object.centerPos;
	}

	@Override
	public double getCurrentBrightness(Wavelength arg0) {
		return 1.0;
	}

	@Override
	public SpCoord getCurrentHorizontalPos() {
		return this.appPos;
	}

	@Override
	public double getCurrentPhase() {
		return 1.0;
	}

	@Override
	public CelestialPeriod getHorizontalPeriod() {
		return this.horizontalPeriod;
	}

	@Override
	public EnumCelestialObjectType getObjectType() {
		return EnumCelestialObjectType.DeepSkyObject;
	}

	@Override
	public CelestialPeriod getPhasePeriod() {
		return null;
	}

	@Override
	public double getStandardMagnitude() {
		return object.magnitude;
	}

}
