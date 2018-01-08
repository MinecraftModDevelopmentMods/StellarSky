package stellarium.stellars.layer;

import stellarapi.api.CelestialPeriod;
import stellarapi.api.ICelestialCoordinates;
import stellarapi.api.ISkyEffect;
import stellarapi.api.celestials.ICelestialObject;

public interface IPerWorldImage<Obj extends StellarObject> extends ICelestialObject {
	
	public void initialize(Obj object, ICelestialCoordinates coordinate, ISkyEffect sky, CelestialPeriod yearPeriod);
	public void updateCache(Obj object, ICelestialCoordinates coordinate, ISkyEffect sky);

}
