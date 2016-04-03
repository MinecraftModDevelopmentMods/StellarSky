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
	
	//Axial tilt
	public static final double e=0.4090926;
	//Precession
	public static final double prec=0.0;
	
	//Rotation
	private double rot;
	
	//Length
	private double yearLength, dayLength;
	
	//Coordinate
	private double latitude, longitude;
	
	private boolean hideObjectsUnderHorizon;
	
	public NonRefractiveViewpoint(CommonSettings commonSettings, PerDimensionSettings settings) {
		this.yearLength = commonSettings.year;
		this.dayLength = commonSettings.day;
		this.latitude = Spmath.Radians(settings.latitude);
		this.longitude = Spmath.Radians(settings.longitude);
		this.rot = 2 * Math.PI * (this.yearLength + 1);
		
		this.hideObjectsUnderHorizon = settings.hideObjectsUnderHorizon;
	}

	@Override
	public void update(World world, double year) {
		ZTEctoNEc.setRAngle(-prec*year);
		NEctoZTEc.setRAngle(prec*year);
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
		EastEq.set(getInvTransformedEq(East));
		
		EVector NorthEq = new EVector(0.0, 1.0, 0.0);
		NorthEq.set(getInvTransformedEq(North));
		
		EVector ZenEq = new EVector(0.0,0.0,1.0);
		ZenEq.set(getInvTransformedEq(ZenEq));

		this.projectionEq = new EProjection(East, North, ZenD);
		
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
	public IEVector ZenD;
	
	//Vector from Earth center to Ground
	@Deprecated
	public EVector Zen = new EVector(3);
	
	
	//Equatorial to Ecliptic
	public final Rotate EqtoEc = new Rotate('X').setRAngle(-e); 
	
	//Ecliptic to Equatorial
	public final Rotate EctoEq = new Rotate('X').setRAngle(e); 
	
	
	//Zero Time Ecliptic to Now Ecliptic
	public Rotate ZTEctoNEc = new Rotate('Z');

	//Now Ecliptic to Zero Time Ecliptic
	public Rotate NEctoZTEc = new Rotate('Z');


	//Now Equatorial to Rotating Equatorial
	public Rotate NEqtoREq = new Rotate('Z');
	
	//Rotating Equatorial to Now Equatorial
	public Rotate REqtoNEq = new Rotate('Z');

	
	//Rotating Equatorial to Horizontal
	public Rotate REqtoHor = new Rotate('X');
	
	//Horizontal to Rotating Equatorial
	public Rotate HortoREq = new Rotate('X');
	
	public EProjection projection;
	public EProjection projectionEq;

}
