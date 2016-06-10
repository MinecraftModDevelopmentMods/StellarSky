package stellarium.stellars.system;

import stellarapi.api.lib.math.Matrix3;
import stellarapi.api.lib.math.Spmath;
import stellarapi.api.lib.math.Vector3;
import stellarium.util.math.StellarMath;

public class Moon extends SolarObject {
	
	//Additional Orbital Elements for Moon
	protected double a0, e0, I0, w0, Omega0, M0_0;
	protected double wd, Omegad;
	
	protected double a, e, I, w, Omega, M0;
	protected double mean_mot;
	protected double rotation;
	
	protected double brightness;
	
	protected double brightnessFactor;
	
	private double currentOffsetAngle;
		
	//Moon's Ecliptic Position Vector from Ground
	//EVector posGround = new EVector(3);
	
	
	//Moon's Pole(Ecliptic Coord)
	Vector3 Pole;
	
	//Moon's Prime Meridian at first
	Vector3 PrMer0 = new Vector3();
	
	//Moon's East from Prime Meridian
	Vector3 East = new Vector3();
	
	Matrix3 ri = new Matrix3(), rom = new Matrix3(), rw = new Matrix3();


	public Moon(String name, SolarObject earth) {
		super(name, earth);
	}
	
	@Override
	public void initialize() {
		super.initialize();
		Pole = new Vector3(0.0, 0.0, 1.0);
		ri.setAsRotation(1.0, 0.0, 0.0, - Spmath.Radians(I0));
		rom.setAsRotation(0.0, 0.0, 1.0, -Spmath.Radians(Omega0));
		
		ri.transform(this.Pole);
		rom.transform(this.Pole);
		
		PrMer0 = this.getRelativePos(0.0);
		PrMer0.scale(-1.0);
		PrMer0.normalize();
		East.setCross(Pole, PrMer0);
		
		this.mean_mot=360.0*Math.sqrt(parent.mass/a)/a;
	}
	
	public double getRevolutionPeriod() {
		return 360.0 / this.mean_mot;
	}
	
	//Get Ecliptic Position Vector from Earth
	public Vector3 getRelativePos(double yr){
		this.updateOrbE(yr);
		this.rotation = mean_mot * yr;
		double M = this.M0 + this.rotation;
		this.currentOffsetAngle = M + w;
		
		ri.setAsRotation(1.0, 0.0, 0.0, -Spmath.Radians(I));
		rw.setAsRotation(0.0, 0.0, 1.0, -Spmath.Radians(w));
		rom.setAsRotation(0.0, 0.0, 1.0, -Spmath.Radians(Omega));

		Matrix3 matrix = new Matrix3();
		matrix.setIdentity();
		matrix.postMult(this.rom);
		matrix.postMult(this.ri);
		matrix.postMult(this.rw);
		
		return StellarMath.GetOrbVec(a, e, M, matrix);
	}
	
	//Update Orbital Elements in time
	private void updateOrbE(double yr){
		a=a0;
		e=e0;
		I=I0;
		w=w0+wd*yr;
		Omega=Omega0+Omegad*yr;
		M0=M0_0;
	}
	
	@Override
	public void updateModulate() {
		Vector3 vec = new Vector3(this.relativePos);
		vec.scale(this.mass / parent.mass);
		parent.relativePos.sub(vec);
	}
	
	//Ecliptic Position of Moon's Local Region from Moon Center (Update Needed)
	public Vector3 posLocalM(double longitude, double latitude){
		float longp=(float)Spmath.Radians(longitude + this.rotation);
		float lat=(float)Spmath.Radians(latitude);
		Vector3 result = new Vector3(this.Pole);
		result.scale(Spmath.sinf(lat));
		Vector3 ref = new Vector3(this.PrMer0);
		ref.scale(Spmath.cosf(lat)*Spmath.cosf(longp));
		result.add(ref);
		ref = new Vector3(this.East);
		ref.scale(Spmath.cosf(lat)*Spmath.sinf(longp));
		result.add(ref);
		result.scale(this.radius);
		return result;
	}
	
	//Ecliptic Position of Moon's Local Region from Ground (Update Needed)
	//Parameter: PosLocalM Result
	public Vector3 posLocalG(Vector3 p){
		return new Vector3(p).add(this.earthPos);
	}
	
	@Override
	protected void updateMagnitude(Vector3 earthFromSun) {
		double dist=this.earthPos.size();
		double distS=this.sunPos.size();
		double distE=earthFromSun.size();
		double LvsSun=this.radius*this.radius*this.getPhase()*distE*distE*this.albedo*this.brightnessFactor*1.4/(dist*dist*distS*distS);
		this.currentMag=-26.74-2.5*Math.log10(LvsSun);
		this.brightness = distE*distE*this.albedo * this.brightnessFactor/(distS*distS)*10;
	}
	
	//Illumination of Moon's Local Region (Update Needed)
	//Parameter: PosLocalM Result
	public double illumination(Vector3 p){
		return -this.sunPos.dot(p)/(this.sunPos.size()*p.size())*this.brightness;
	}
	
	@Override
	public double absoluteOffset() {
		return this.currentOffsetAngle / 360.0;
	}
	
	//Phase of the Moon(Update Needed)
	@Override
	public double getPhase(){
		return (Math.PI-Math.acos(earthPos.dot(this.sunPos) / (this.earthPos.size() * this.sunPos.size()))) / Math.PI;
	}
	
	//Time phase for moon
	@Override
	public double phaseOffset(){
		Vector3 crossed = new Vector3();
		crossed.setCross(this.earthPos, this.sunPos);
		double k=Math.signum(crossed.dot(Pole)) * (1.0 - getPhase());
		if(k<0) k=k+2;
		return k/2;
	}

}
