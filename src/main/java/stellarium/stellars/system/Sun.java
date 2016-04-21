package stellarium.stellars.system;

import javax.vecmath.Vector3d;

import stellarium.stellars.layer.IRenderCache;

public class Sun extends SolarObject {

	public Sun(String name) {
		super(name);
		//Constant for sun
		this.currentMag=-26.74;
	}

	@Override
	public Vector3d getRelativePos(double year) {
		return null;
	}
	
	protected void updateMagnitude(Vector3d earthFromSun) { }

	public double getMagnitude() {
		return this.currentMag;
	}

}
