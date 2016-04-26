package stellarium.stellars.star;

import stellarapi.api.lib.math.Vector3;
import stellarium.stellars.layer.StellarObject;

public class BgStar extends StellarObject {

	protected String name;
	protected double mag;
	protected double B_V;
	protected Vector3 pos;
	
	public BgStar(String name, double mag, double B_V, Vector3 pos) {
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
