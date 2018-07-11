package stellarium.stellars.system;

import stellarapi.api.CelestialPeriod;
import stellarapi.api.celestials.EnumObjectType;
import stellarapi.api.lib.math.Vector3;
import stellarapi.api.view.ICCoordinates;

public class Sun extends SolarObject {

	protected double offset;
	private double rotation;
	private static final double rotationSpeed = 14.7 * 365.2422;

	public Sun(String name) {
		super(name, EnumObjectType.Star);
		//Constant for sun
		this.currentMag=-26.74;
		this.setStandardMagnitude(this.currentMag);
	}

	@Override
	public void setupCoord(ICCoordinates coords, CelestialPeriod yearPeriod) {
		this.setAbsolutePeriod(new CelestialPeriod("Year", yearPeriod.getPeriodLength(), this.absoluteOffset()));
		CelestialPeriod dayPeriod = coords.getPeriod();
		double length = 1 / (1 / dayPeriod.getPeriodLength() - 1 / yearPeriod.getPeriodLength());
		this.setHoritontalPeriod(new CelestialPeriod("Day", length, coords.calculateInitialOffset(this.earthPos, length)));
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
		double longp = Math.toRadians(longitude + this.rotation);
		double lat = Math.toRadians(latitude);
		Vector3 result = new Vector3(0.0, 0.0, 1.0);
		result.scale(Math.sin(lat));
		Vector3 ref = new Vector3(1.0, 0.0, 0.0);
		ref.scale(Math.cos(lat)*Math.cos(longp));
		result.add(ref);
		ref = new Vector3(0.0, 1.0, 0.0);
		ref.scale(Math.cos(lat)*Math.sin(longp));
		result.add(ref);
		result.scale(this.radius);
		return result;
	}

}
