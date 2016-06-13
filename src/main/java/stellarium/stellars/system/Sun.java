package stellarium.stellars.system;

import stellarapi.api.lib.math.Spmath;
import stellarapi.api.lib.math.Vector3;

public class Sun extends SolarObject {

	protected double offset;
	private double rotation;
	private static final double rotationSpeed = 14.7 * 365.2422;

	public Sun(String name) {
		super(name);
		//Constant for sun
		this.currentMag=-26.74;
	}

	@Override
	public void updatePre(double year) {
		super.updatePre(year);
		this.rotation = rotationSpeed * year;
	}

	@Override
	public Vector3 getRelativePos(double year) {
		return null;
	}
	
	@Override
	public void updatePost(SolarObject earth) {
		this.offset = earth.absoluteOffset();
	}
	
	@Override
	protected void updateMagnitude(Vector3 earthFromSun) { }

	public double getMagnitude() {
		return this.currentMag;
	}

	@Override
	public double absoluteOffset() {
		return this.offset;
	}
	
	public Vector3 posLocalSun(double longitude, double latitude){
		float longp=(float)Spmath.Radians(longitude + this.rotation);
		float lat=(float)Spmath.Radians(latitude);
		Vector3 result = new Vector3(0.0, 0.0, 1.0);
		result.scale(Spmath.sinf(lat));
		Vector3 ref = new Vector3(1.0, 0.0, 0.0);
		ref.scale(Spmath.cosf(lat)*Spmath.cosf(longp));
		result.add(ref);
		ref = new Vector3(0.0, 1.0, 0.0);
		ref.scale(Spmath.cosf(lat)*Spmath.sinf(longp));
		result.add(ref);
		result.scale(this.radius);
		return result;
	}

}
