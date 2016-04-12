package stellarium.stellars.view;

import net.minecraft.world.World;
import sciapi.api.value.IValRef;
import sciapi.api.value.euclidian.EProjection;
import sciapi.api.value.euclidian.EVector;
import sciapi.api.value.euclidian.IEVector;
import stellarium.common.CommonSettings;
import stellarium.util.math.Rotate;
import stellarium.util.math.SpCoord;
import stellarium.util.math.Spmath;

public class NonRefractiveViewpoint implements IStellarViewpoint {
	
	//Rotation
	private double rot;
	
	//Length
	private double yearLength, dayLength;
	
	//Coordinate
	private double latitude, longitude;
	
	private double axialTilt, precession;
	
	private boolean hideObjectsUnderHorizon;
	
	public NonRefractiveViewpoint(CommonSettings commonSettings, PerDimensionSettings settings) {
		this.yearLength = commonSettings.year;
		this.dayLength = commonSettings.day;
		this.latitude = Spmath.Radians(settings.latitude);
		this.longitude = Spmath.Radians(settings.longitude);
		this.rot = 2 * Math.PI * (this.yearLength + 1);
		this.axialTilt = Spmath.Radians(commonSettings.propAxialTilt.getDouble());
		this.precession = Spmath.Radians(commonSettings.propPrecession.getDouble());
		
		this.EqtoEc = new Rotate('X').setRAngle(-this.axialTilt);
		this.EctoEq = new Rotate('X').setRAngle(this.axialTilt);
		
		this.hideObjectsUnderHorizon = settings.hideObjectsUnderHorizon();
	}

	@Override
	public void update(World world, double year) {
		ZTEctoNEc.setRAngle(-this.precession*year);
		NEctoZTEc.setRAngle(this.precession*year);
		NEqtoREq.setRAngle(-this.rot*year - this.longitude);
		REqtoNEq.setRAngle(this.rot*year + this.longitude);
		
		REqtoHor.setRAngle(this.latitude-Math.PI*0.5);
		HortoREq.setRAngle(Math.PI*0.5-this.latitude);
		
		
		EVector East = new EVector(1.0, 0.0, 0.0);
		East.set(getInvTransformed(East));
		
		EVector North = new EVector(0.0, 1.0, 0.0);
		North.set(getInvTransformed(North));
		
		this.ZenD = new EVector(0.0,0.0,1.0);
		ZenD.set(getInvTransformed(ZenD));

		this.projection = new EProjection(East, North, ZenD);
		
		
		EVector EastEq = new EVector(1.0, 0.0, 0.0);
		EastEq.set(getInvTransformedEq(EastEq));
		
		EVector NorthEq = new EVector(0.0, 1.0, 0.0);
		NorthEq.set(getInvTransformedEq(NorthEq));
		
		EVector ZenEq = new EVector(0.0,0.0,1.0);
		ZenEq.set(getInvTransformedEq(ZenEq));

		this.projectionEq = new EProjection(EastEq, NorthEq, ZenEq);
		
		//Zen.set(VOp.mult(manager.Earth.radius, ZenD));
	}

	@Override
	public EProjection getProjection() {
		return this.projection;
	}
	
	@Override
	public EProjection projectionToEquatorial() {
		return this.projectionEq;
	}

	@Override
	public void applyAtmRefraction(SpCoord coord) { }

	@Override
	public void disapplyAtmRefraction(SpCoord coord) { }

	@Override
	public double getAirmass(IValRef<EVector> vector, boolean isRefractionApplied) {
		return 0.0;
	}

	@Override
	public boolean hideObjectsUnderHorizon() {
		return this.hideObjectsUnderHorizon;
	}
	
	private IValRef getInvTransformed(IValRef vec) {
		IValRef ref = HortoREq.transform(vec);
		ref.set(REqtoNEq.transform(ref));
		ref.set(EqtoEc.transform(ref));
		ref.set(NEctoZTEc.transform(ref));
		return ref;
	}
	
	private IValRef getInvTransformedEq(IValRef vec) {
		IValRef ref = EqtoEc.transform(vec);
		ref.set(NEctoZTEc.transform(ref));
		return ref;
	}
	
	
	//Direction of Zenith
	private IEVector ZenD;
	
	//Vector from Earth center to Ground
	@Deprecated
	private EVector Zen = new EVector(3);
	
	
	//Equatorial to Ecliptic
	private final Rotate EqtoEc; 
	
	//Ecliptic to Equatorial
	private final Rotate EctoEq; 
	
	
	//Zero Time Ecliptic to Now Ecliptic
	private Rotate ZTEctoNEc = new Rotate('Z');

	//Now Ecliptic to Zero Time Ecliptic
	private Rotate NEctoZTEc = new Rotate('Z');


	//Now Equatorial to Rotating Equatorial
	private Rotate NEqtoREq = new Rotate('Z');
	
	//Rotating Equatorial to Now Equatorial
	private Rotate REqtoNEq = new Rotate('Z');

	
	//Rotating Equatorial to Horizontal
	private Rotate REqtoHor = new Rotate('X');
	
	//Horizontal to Rotating Equatorial
	private Rotate HortoREq = new Rotate('X');
	
	private EProjection projection;
	private EProjection projectionEq;

}
