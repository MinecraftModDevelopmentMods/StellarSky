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

	@Override
	public CelestialPeriod getAbsolutePeriod() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector3 getCurrentAbsolutePos() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getCurrentBrightness(Wavelength arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public SpCoord getCurrentHorizontalPos() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getCurrentPhase() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public CelestialPeriod getHorizontalPeriod() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EnumCelestialObjectType getObjectType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CelestialPeriod getPhasePeriod() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getStandardMagnitude() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void initialize(DeepSkyObject object, ICelestialCoordinate coordinate, ISkyEffect sky,
			CelestialPeriod yearPeriod) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateCache(DeepSkyObject object, ICelestialCoordinate coordinate, ISkyEffect sky) {
		// TODO Auto-generated method stub
		
	}

}
