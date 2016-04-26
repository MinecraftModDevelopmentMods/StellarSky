package stellarium.stellars.system;

import stellarapi.api.lib.math.Vector3;

public class Sun extends SolarObject {

	public Sun(String name) {
		super(name);
		//Constant for sun
		this.currentMag=-26.74;
	}

	@Override
	public Vector3 getRelativePos(double year) {
		return null;
	}
	
	protected void updateMagnitude(Vector3 earthFromSun) { }

	public double getMagnitude() {
		return this.currentMag;
	}

}
