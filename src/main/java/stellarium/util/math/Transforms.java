package stellarium.util.math;

import sciapi.api.value.IValRef;
import sciapi.api.value.STempRef;
import sciapi.api.value.euclidian.EVector;
import sciapi.api.value.euclidian.EVectorSet;
import sciapi.api.value.euclidian.IEVector;
import sciapi.api.value.util.VOp;
import stellarium.StellarSky;
import stellarium.stellars.StellarManager;

public class Transforms {
	
	//Axial tilt
	public static final double e=0.4090926;
	//Precession
	public static final double Prec=0.0;
	//Rotation
	public static double Rot;
	//Latitude on Overworld
	public static final double Lat=0.6544985;
	//Latitude on Ender
	public static final double Lat2=-0.9162979;
	

	public static double yr;
	
	
	//Set Transforms' time and world (stime:tick)
	public static void Update(double stime, boolean IsOverWorld){
		yr = stime / StellarSky.getManager().day / StellarSky.getManager().year;
		
		Rot = 2 * Math.PI / StellarSky.getManager().day * (1 + 1 / StellarSky.getManager().year);
		
		ZTEctoNEc.setRAngle(-Prec*stime);
		NEctoZTEc.setRAngle(Prec*stime);
		NEqtoREq.setRAngle(-Rot*stime);
		REqtoNEq.setRAngle(Rot*stime);
		if(IsOverWorld){
			REqtoHor.setRAngle(Lat-Math.PI*0.5);
			HortoREq.setRAngle(Math.PI*0.5-Lat);
		}
		else{
			REqtoHor.setRAngle(Lat2-Math.PI*0.5);
			HortoREq.setRAngle(Math.PI*0.5-Lat2);
		}
		
		ZenD = new EVector(0.0,0.0,1.0);
		ZenD.set(HortoREq.transform(ZenD));
		ZenD.set(REqtoNEq.transform(ZenD));
		ZenD.set(EqtoEc.transform(ZenD));
		ZenD.set(NEctoZTEc.transform(ZenD));
		Zen.set(VOp.mult(StellarSky.getManager().Earth.Radius, ZenD));
	}
	
	
	//Direction of Zenith
	public static IEVector ZenD;
	
	//Vector from Earth center to Ground
	public static EVector Zen = new EVector(3);
	
	
	//Equatorial to Ecliptic
	public static final Rotate EqtoEc = new Rotate('X').setRAngle(-e); 
	
	//Ecliptic to Equatorial
	public static final Rotate EctoEq = new Rotate('X').setRAngle(e); 
	
	
	//Zero Time Ecliptic to Now Ecliptic
	public static Rotate ZTEctoNEc = new Rotate('Z');

	//Now Ecliptic to Zero Time Ecliptic
	public static Rotate NEctoZTEc = new Rotate('Z');


	//Now Equatorial to Rotating Equatorial
	public static Rotate NEqtoREq = new Rotate('Z');
	
	//Rotating Equatorial to Now Equatorial
	public static Rotate REqtoNEq = new Rotate('Z');

	
	//Rotating Equatorial to Horizontal
	public static Rotate REqtoHor = new Rotate('X');
	
	//Horizontal to Rotating Equatorial
	public static Rotate HortoREq = new Rotate('X');
	
}
