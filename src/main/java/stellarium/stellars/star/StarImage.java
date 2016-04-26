package stellarium.stellars.star;

import stellarapi.api.CelestialPeriod;
import stellarapi.api.ICelestialCoordinate;
import stellarapi.api.ISkyEffect;
import stellarapi.api.celestials.EnumCelestialObjectType;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Vector3;
import stellarapi.api.optics.Wavelength;
import stellarium.stellars.layer.IPerWorldImage;

public class StarImage implements IPerWorldImage<BgStar> {
	
	private CelestialPeriod horizontalPeriod;
	private BgStar main;
	private SpCoord appPos = new SpCoord();
	
	@Override
	public void initialize(BgStar object, ICelestialCoordinate coordinate, ISkyEffect effect, CelestialPeriod year) {
		this.main = object;
		
		SpCoord coord = new SpCoord();
		coord.setWithVec(object.pos);
		this.horizontalPeriod = new CelestialPeriod(String.format("Day; Star %s", main.name),
				coordinate.getPeriod().getPeriodLength(),
				coordinate.calculateInitialOffset(object.pos, coordinate.getPeriod().getPeriodLength()));
	}
	
	@Override
	public void updateCache(BgStar object, ICelestialCoordinate coordinate, ISkyEffect sky) {
		Vector3 ref = new Vector3(object.pos);
		coordinate.getProjectionToGround().transform(ref);
		appPos.setWithVec(ref);
		sky.applyAtmRefraction(this.appPos);
	}
	
	@Override
	public CelestialPeriod getAbsolutePeriod() {
		return null;
	}

	@Override
	public CelestialPeriod getHorizontalPeriod() {
		return this.horizontalPeriod;
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
		return main.pos;
	}

	@Override
	public SpCoord getCurrentHorizontalPos() {
		return this.appPos;
	}

	@Override
	public double getStandardMagnitude() {
		return main.mag;
	}

	@Override
	public EnumCelestialObjectType getObjectType() {
		return EnumCelestialObjectType.Star;
	}
}
