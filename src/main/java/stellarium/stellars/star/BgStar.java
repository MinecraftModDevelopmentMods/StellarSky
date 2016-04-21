package stellarium.stellars.star;

import javax.vecmath.Vector3d;

import stellarapi.api.CelestialPeriod;
import stellarapi.api.celestials.EnumCelestialObjectType;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.optics.Wavelength;
import stellarium.stellars.layer.StellarObject;
import stellarium.stellars.layer.IRenderCache;

public class BgStar extends StellarObject {

	protected String name;
	protected double mag, B_V;
	protected Vector3d pos;
	
	public BgStar(String name, double mag, double B_V, Vector3d pos) {
		this.name = name;
		this.mag = mag;
		this.B_V = B_V;
		this.pos = pos;
	}

	@Override
	public String getID() {
		return this.name;
	}

}
