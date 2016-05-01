package stellarium.stellars.system;

import stellarapi.api.lib.math.Vector3;

public class Sun extends SolarObject {

	protected double offset;
	
	public Sun(String name) {
		super(name);
		//Constant for sun
		this.currentMag=-26.74;
	}

	@Override
	public Vector3 getRelativePos(double year) {
		return null;
	}
	
	public void updatePost(SolarObject earth) {
		this.offset = earth.absoluteOffset();
	}
	
	protected void updateMagnitude(Vector3 earthFromSun) { }

	public double getMagnitude() {
		return this.currentMag;
	}

	@Override
	public double absoluteOffset() {
		return this.offset;
	}

}
