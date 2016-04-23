package stellarium.stellars.star;

import javax.vecmath.Vector3d;

import stellarapi.api.optics.WaveExtensive;
import stellarapi.api.optics.Wavelength;
import stellarium.stellars.layer.StellarObject;

public class BgStar extends StellarObject {

	protected String name;
	protected double mag;
	protected double B_V;
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
