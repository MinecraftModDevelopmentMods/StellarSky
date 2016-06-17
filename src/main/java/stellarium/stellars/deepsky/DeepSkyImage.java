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
	private Vector3 absPos;
	private SpCoord appPos;
	private double magnitude;
	private String name;
	
	@Override
	public void initialize(DeepSkyObject object, ICelestialCoordinate coordinate, ISkyEffect sky,
			CelestialPeriod yearPeriod) {
		this.object = object;
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
		return this.absPos;
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
		return this.magnitude;
	}

}
