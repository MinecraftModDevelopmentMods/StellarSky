package stellarium.stellars.system;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

import stellarapi.api.lib.math.Spmath;
import stellarium.stellars.layer.IRenderCache;
import stellarium.util.math.StellarMath;

public class Moon extends SolarObject {
	
	//Additional Orbital Elements for Moon
	protected double a0, e0, I0, w0, Omega0, M0_0;
	protected double wd, Omegad;
	
	protected double a, e, I, w, Omega, M0;
	protected double mean_mot;
	protected double rotation;
	
	protected double brightness;
		
	//Moon's Ecliptic Position Vector from Ground
	//EVector posGround = new EVector(3);
	
	
	//Moon's Pole(Ecliptic Coord)
	Vector3d Pole;
	
	//Moon's Prime Meridian at first
	Vector3d PrMer0 = new Vector3d();
	
	//Moon's East from Prime Meridian
	Vector3d East = new Vector3d();
	
	Matrix3d ri = new Matrix3d(), rom = new Matrix3d(), rw = new Matrix3d();


	public Moon(String name, SolarObject earth) {
		super(name, earth);
	}
	
	@Override
	public void initialize() {
		super.initialize();
		Pole = new Vector3d(0.0, 0.0, 1.0);
		ri.set(new AxisAngle4d(1.0, 0.0, 0.0, - Spmath.Radians(I0)));
		rom.set(new AxisAngle4d(0.0, 0.0, 1.0, -Spmath.Radians(Omega0)));
		
		ri.transform(this.Pole);
		rom.transform(this.Pole);
		
		PrMer0 = this.getRelativePos(0.0);
		PrMer0.scale(-1.0);
		PrMer0.normalize();
		East.cross(Pole, PrMer0);
	}
	
	//Get Ecliptic Position Vector from Earth
	public Vector3d getRelativePos(double yr){
		this.updateOrbE(yr);
		this.rotation = mean_mot * yr;
		double M = this.M0 + this.rotation;
		
		ri.set(new AxisAngle4d(1.0, 0.0, 0.0, -Spmath.Radians(I)));
		rw.set(new AxisAngle4d(0.0, 0.0, 1.0, -Spmath.Radians(w)));
		rom.set(new AxisAngle4d(0.0, 0.0, 1.0, -Spmath.Radians(Omega)));

		Matrix3d matrix = new Matrix3d();
		matrix.setIdentity();
		matrix.mul(this.rom);
		matrix.mul(this.ri);
		matrix.mul(this.rw);
		
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
		this.mean_mot=360.0*Math.sqrt(parent.mass/a)/a;
	}
	
	@Override
	public void updateModulate() {
		Vector3d vec = new Vector3d(this.relativePos);
		vec.scale(this.mass / parent.mass);
		parent.relativePos.sub(vec);
	}
	
	//Ecliptic Position of Moon's Local Region from Moon Center (Update Needed)
	public Vector3d posLocalM(double longitude, double latitude){
		float longp=(float)Spmath.Radians(longitude + this.rotation);
		float lat=(float)Spmath.Radians(latitude);
		Vector3d result = new Vector3d(this.Pole);
		result.scale(Spmath.sinf(lat));
		Vector3d ref = new Vector3d(this.PrMer0);
		ref.scale(Spmath.cosf(lat)*Spmath.cosf(longp));
		result.add(ref);
		ref = new Vector3d(this.East);
		ref.scale(Spmath.cosf(lat)*Spmath.sinf(longp));
		result.add(ref);
		result.scale(this.radius);
		return result;
	}
	
	//Ecliptic Position of Moon's Local Region from Ground (Update Needed)
	//Parameter: PosLocalM Result
	public Vector3d posLocalG(Vector3d p){
		Vector3d vec = new Vector3d(p);
		vec.add(this.earthPos);
		return vec;
	}
	
	@Override
	protected void updateMagnitude(Vector3d earthFromSun) {
		double dist=this.earthPos.length();
		double distS=this.sunPos.length();
		double distE=earthFromSun.length();
		double LvsSun=this.radius*this.radius*this.getPhase()*distE*distE*this.albedo*1.4/(dist*dist*distS*distS);
		this.currentMag=-26.74-2.5*Math.log10(LvsSun);
		this.brightness = distE*distE*this.albedo/(distS*distS)*10;
	}
	
	//Illumination of Moon's Local Region (Update Needed)
	//Parameter: PosLocalM Result
	public double illumination(Vector3d p){
		return -this.sunPos.dot(p)/(this.sunPos.length()*p.length())*this.brightness;
	}
	
	//Phase of the Moon(Update Needed)
	@Override
	public double getPhase(){
		return (Math.PI-Math.acos(earthPos.dot(this.sunPos) / (this.earthPos.length() * this.sunPos.length()))) / Math.PI;
	}
	
	//Time phase for moon
	public double phase_Time(){
		Vector3d crossed = new Vector3d();
		crossed.cross(this.earthPos, this.sunPos);
		double k=Math.signum(crossed.dot(Pole)) * (1.0 - getPhase());
		if(k<0) k=k+2;
		return k/2;
	}

	public double getPeriod() {
		return this.a * Math.sqrt(this.a / parent.mass);
	}

}
