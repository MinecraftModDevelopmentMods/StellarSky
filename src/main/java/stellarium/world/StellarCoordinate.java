package stellarium.world;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

import net.minecraft.world.World;
import stellarapi.api.CelestialPeriod;
import stellarapi.api.ICelestialCoordinate;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Spmath;
import stellarium.common.CommonSettings;

public class StellarCoordinate implements ICelestialCoordinate {
	
	//Rotation
	private double rot;
	
	//Length
	private double yearLength, dayLength;
	
	//Coordinate
	private double latitude, longitude;
	
	private double axialTilt, precession;
	
	private double zeroTime;
	
	private CelestialPeriod dayPeriod;
	private CelestialPeriod yearPeriod;
	
	public StellarCoordinate(CommonSettings commonSettings, PerDimensionSettings settings) {
		this.yearLength = commonSettings.year;
		this.dayLength = commonSettings.day;
		this.latitude = Spmath.Radians(settings.latitude);
		this.longitude = Spmath.Radians(settings.longitude);
		this.rot = 2 * Math.PI * (this.yearLength + 1);
		this.axialTilt = Spmath.Radians(commonSettings.propAxialTilt.getDouble());
		this.precession = Spmath.Radians(commonSettings.propPrecession.getDouble());
		
		this.zeroTime = (commonSettings.yearOffset * commonSettings.year + commonSettings.dayOffset) * commonSettings.day + commonSettings.tickOffset;
		
		double fixedDaylength = this.dayLength * this.yearLength / (this.yearLength + 1);
		this.dayPeriod = new CelestialPeriod("Celestial Day", fixedDaylength,
				Spmath.fmod(this.zeroTime / fixedDaylength - this.longitude / 2 / Math.PI - 0.25, 1.0));
		
		double fixedYearLength = this.dayLength * this.yearLength / (1.0 - this.precession / 2 / Math.PI);
		this.yearPeriod = new CelestialPeriod("Celestial Year", fixedYearLength,
				Spmath.fmod(this.zeroTime / fixedYearLength - this.longitude / 2 / Math.PI - 0.25, 1.0));
		
		EqtoEc.set(new AxisAngle4d(1.0, 0.0, 0.0, -this.axialTilt));
		EctoEq.set(new AxisAngle4d(1.0, 0.0, 0.0, this.axialTilt));
	}
	
	public CelestialPeriod getYearPeriod() {
		return this.yearPeriod;
	}
	
	public void update(double year) {
		ZTEctoNEc.set(new AxisAngle4d(0.0, 0.0, 1.0, -this.precession*year));
		NEctoZTEc.set(new AxisAngle4d(0.0, 0.0, 1.0, this.precession*year));
		NEqtoREq.set(new AxisAngle4d(0.0, 0.0, 1.0, -this.rot*year - this.longitude));
		REqtoNEq.set(new AxisAngle4d(0.0, 0.0, 1.0, this.rot*year + this.longitude));
		
		REqtoHor.set(new AxisAngle4d(1.0, 0.0, 0.0, this.latitude));
		HortoREq.set(new AxisAngle4d(1.0, 0.0, 0.0, -this.latitude));
		
		Vector3d East = new Vector3d(1.0, 0.0, 0.0);
		invtransform(East);
		
		Vector3d North = new Vector3d(0.0, 1.0, 0.0);
		invtransform(North);
		
		this.ZenD = new Vector3d(0.0, 0.0, 1.0);
		invtransform(ZenD);

		projection.setRow(0, East);
		projection.setRow(1, North);
		projection.setRow(2, ZenD);
		
		Vector3d EastEq = new Vector3d(1.0, 0.0, 0.0);
		invtransformEq(EastEq);
		
		Vector3d NorthEq = new Vector3d(0.0, 1.0, 0.0);
		invtransformEq(NorthEq);
		
		Vector3d ZenEq = new Vector3d(0.0,0.0,1.0);
		invtransformEq(ZenEq);

		projectionEq.setRow(0, EastEq);
		projectionEq.setRow(1, NorthEq);
		projectionEq.setRow(2, ZenEq);
		
		//Zen.set(VOp.mult(manager.Earth.radius, ZenD));
	}
	
	private void invtransform(Vector3d vec) {
		HortoREq.transform(vec);
		REqtoNEq.transform(vec);
		EqtoEc.transform(vec);
		NEctoZTEc.transform(vec);
	}
	
	private void invtransformEq(Vector3d vec) {
		EqtoEc.transform(vec);
		NEctoZTEc.transform(vec);
	}
	
	
	//Direction of Zenith
	private Vector3d ZenD;
	
	//Vector from Earth center to Ground
	@Deprecated
	private Vector3d Zen = new Vector3d();
	
	
	//Equatorial to Ecliptic
	private final Matrix3d EqtoEc = new Matrix3d(); 
	
	//Ecliptic to Equatorial
	private final Matrix3d EctoEq = new Matrix3d(); 
	
	
	//Zero Time Ecliptic to Now Ecliptic
	private Matrix3d ZTEctoNEc = new Matrix3d();

	//Now Ecliptic to Zero Time Ecliptic
	private Matrix3d NEctoZTEc = new Matrix3d();


	//Now Equatorial to Rotating Equatorial
	private Matrix3d NEqtoREq = new Matrix3d();
	
	//Rotating Equatorial to Now Equatorial
	private Matrix3d REqtoNEq = new Matrix3d();

	
	//Rotating Equatorial to Horizontal
	private Matrix3d REqtoHor = new Matrix3d();
	
	//Horizontal to Rotating Equatorial
	private Matrix3d HortoREq = new Matrix3d();
	
	private Matrix3d projection = new Matrix3d();
	private Matrix3d projectionEq = new Matrix3d();

	
	@Override
	public Matrix3d getProjectionToGround() {
		return this.projection;
	}

	@Override
	public CelestialPeriod getPeriod() {
		return this.dayPeriod;
	}
	
	@Override
	public double calculateInitialOffset(Vector3d initialAbsPos, double periodLength) {
		Vector3d eqrPos = new Vector3d(initialAbsPos);
		projectionEq.transform(eqrPos);
		
		SpCoord coord = new SpCoord();
		coord.setWithVec(eqrPos);
		
		return Spmath.fmod(this.zeroTime / periodLength - this.longitude / Math.PI - coord.x / 360.0 - 0.25, 1.0);
	}

	@Override
	public double getHighestHeightAngle(Vector3d absPos) {
		Vector3d eqrPos = new Vector3d(absPos);
		projectionEq.transform(eqrPos);
		
		SpCoord coord = new SpCoord();
		coord.setWithVec(eqrPos);
		
		return 90.0 - Math.abs(this.latitude - coord.y);
	}

	@Override
	public double getLowestHeightAngle(Vector3d absPos) {
		Vector3d eqrPos = new Vector3d(absPos);
		projectionEq.transform(eqrPos);
		
		SpCoord coord = new SpCoord();
		coord.setWithVec(eqrPos);
		
		return Math.abs(this.latitude + coord.y) - 90.0;
	}

	@Override
	public double offsetTillObjectReach(Vector3d absPos, double heightAngle) {		
		Vector3d eqrPos = new Vector3d(absPos);
		projectionEq.transform(eqrPos);
		
		SpCoord coord = new SpCoord();
		coord.setWithVec(eqrPos);
		
		return this.hourAngleForHeight(heightAngle, Spmath.Radians(coord.y), Spmath.Radians(this.latitude)) / (2 * Math.PI);
	}
	
	private double hourAngleForHeight(double heightAngle, double dec, double lat) {
		return Math.acos((- Spmath.sind(heightAngle) + Math.sin(dec) * Math.sin(lat)) / (Math.cos(dec) * Math.cos(lat)));
	}

}
