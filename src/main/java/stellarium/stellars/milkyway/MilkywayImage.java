package stellarium.stellars.milkyway;

import javax.vecmath.Vector3d;

import stellarapi.api.CelestialPeriod;
import stellarapi.api.ICelestialCoordinate;
import stellarapi.api.ISkyEffect;
import stellarapi.api.celestials.EnumCelestialObjectType;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.optics.IViewScope;
import stellarapi.api.optics.Wavelength;
import stellarium.stellars.layer.StellarObject;
import stellarium.stellars.layer.IPerWorldImage;

public class MilkywayImage implements IPerWorldImage<Milkyway> {
	
	private Milkyway milkyway;
	private CelestialPeriod period;
	
	@Override
	public void initialize(Milkyway object, ICelestialCoordinate coordinate, ISkyEffect sky, CelestialPeriod year) {
		this.milkyway = object;
		this.period = coordinate.getPeriod();
	}

	@Override
	public void updateCache(Milkyway object, ICelestialCoordinate coordinate, ISkyEffect sky) { }
	
	@Override
	public CelestialPeriod getAbsolutePeriod() {
		return null;
	}

	@Override
	public CelestialPeriod getHorizontalPeriod() {
		return this.period;
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
		return new Vector3d(1.0, 0.0, 0.0);
	}

	@Override
	public SpCoord getCurrentHorizontalPos() {
		return new SpCoord(0.0, 0.0);
	}

	@Override
	public double getStandardMagnitude() {
		return 4.5;
	}

	@Override
	public EnumCelestialObjectType getObjectType() {
		return EnumCelestialObjectType.DeepSkyObject;
	}
}
