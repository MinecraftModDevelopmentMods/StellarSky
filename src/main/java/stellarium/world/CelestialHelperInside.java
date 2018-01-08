package stellarium.world;

import stellarapi.api.ICelestialCoordinates;
import stellarapi.api.ISkyEffect;
import stellarapi.api.celestials.ICelestialObject;
import stellarapi.example.CelestialHelper;
import stellarium.api.ICelestialHelper;

public class CelestialHelperInside extends CelestialHelper implements ICelestialHelper {

	public CelestialHelperInside(float relativeMultiplierSun, float relativeMultiplierMoon, ICelestialObject sun,
			ICelestialObject moon, ICelestialCoordinates coordinate, ISkyEffect sky) {
		super(relativeMultiplierSun, relativeMultiplierMoon, sun, moon, coordinate, sky);
	}

}
