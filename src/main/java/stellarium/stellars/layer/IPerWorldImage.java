package stellarium.stellars.layer;

import stellarapi.api.CelestialPeriod;
import stellarapi.api.ICelestialCoordinate;
import stellarapi.api.ISkyEffect;
import stellarapi.api.celestials.ICelestialObject;

public interface IPerWorldImage<Obj extends StellarObject> extends ICelestialObject {
	
	public void initialize(Obj object, ICelestialCoordinate coordinate, ISkyEffect sky, CelestialPeriod yearPeriod);
	public void updateCache(Obj object, ICelestialCoordinate coordinate, ISkyEffect sky);

}
