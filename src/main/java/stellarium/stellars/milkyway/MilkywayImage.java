package stellarium.stellars.milkyway;

import stellarapi.api.CelestialPeriod;
import stellarapi.api.ICCoordinates;
import stellarapi.api.IAtmosphereEffect;
import stellarapi.api.celestials.EnumCelestialObjectType;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Vector3;
import stellarapi.api.optics.Wavelength;
import stellarium.stellars.layer.IPerWorldImage;

public class MilkywayImage implements IPerWorldImage<Milkyway> {
	private CelestialPeriod period;
	
	@Override
	public void initialize(Milkyway object, ICCoordinates coordinate, IAtmosphereEffect sky, CelestialPeriod year) {
		this.period = coordinate.getPeriod();
	}

	@Override
	public void updateCache(Milkyway object, ICCoordinates coordinate, IAtmosphereEffect sky) { }
	
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
	public Vector3 getCurrentAbsolutePos() {
		return new Vector3(1.0, 0.0, 0.0);
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

	@Override
	public String getName() {
		return "Milkyway";
	}
}
