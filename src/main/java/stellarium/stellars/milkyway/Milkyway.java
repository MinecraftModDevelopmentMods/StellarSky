package stellarium.stellars.milkyway;

import javax.vecmath.Vector3d;

import stellarapi.api.CelestialPeriod;
import stellarapi.api.celestials.EnumCelestialObjectType;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.optics.Wavelength;
import stellarium.stellars.layer.StellarObject;
import stellarium.stellars.layer.IRenderCache;

public class Milkyway extends StellarObject {

	@Override
	public String getID() {
		return "Milkyway";
	}

}
