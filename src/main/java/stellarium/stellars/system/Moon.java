package stellarium.stellars.system;

import sciapi.api.value.IValRef;
import sciapi.api.value.euclidian.CrossUtil;
import sciapi.api.value.euclidian.EVector;
import sciapi.api.value.euclidian.IEVector;
import sciapi.api.value.util.BOp;
import sciapi.api.value.util.VOp;
import stellarium.render.IRenderCache;
import stellarium.util.math.Rotate;
import stellarium.util.math.Spmath;
import stellarium.util.math.VecMath;

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
	EVector Pole;
	
	//Moon's Prime Meridian at first
	EVector PrMer0 = new EVector(3);
	
	//Moon's East from Prime Meridian
	EVector East = new EVector(3);
	
	Rotate ri = new Rotate('X'), rom = new Rotate('Z'), rw = new Rotate('Z');


	public Moon(boolean isRemote, SolarObject earth) {
		super(isRemote, earth);
	}
	
	@Override
	public void initialize() {
		super.initialize();
		Pole=new EVector(0.0, 0.0, 1.0);
		ri.setRAngle(-Spmath.Radians(I0));
		rom.setRAngle(-Spmath.Radians(Omega0));
		Pole.set((IValRef)rom.transform(ri.transform((IEVector)Pole)));
		PrMer0.set(VecMath.normalize(VecMath.mult(-1.0, this.getRelativePos(0.0))));
		East.set((IValRef)CrossUtil.cross((IEVector)Pole, (IEVector)PrMer0));
	}
	
	//Get Ecliptic Position Vector from Earth
	public EVector getRelativePos(double yr){
		this.updateOrbE(yr);
		this.rotation = mean_mot * yr;
		double M = this.M0 + this.rotation;
		return Spmath.GetOrbVec(a, e, ri.setRAngle(-Spmath.Radians(I)), rw.setRAngle(-Spmath.Radians(w)), rom.setRAngle(-Spmath.Radians(Omega)), M);
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
		VecMath.sub(parent.relativePos, VecMath.mult(this.mass / parent.mass, this.relativePos));
	}
	
	@Override
	public IRenderCache generateCache() {
		return new MoonRenderCache();
	}

	@Override
	public int getRenderId() {
		return LayerSolarSystem.moonRenderId;
	}
	
	//Ecliptic Position of Moon's Local Region from Moon Center (Update Needed)
	public IValRef<EVector> posLocalM(double longitude, double latitude){
		float longp=(float)Spmath.Radians(longitude + this.rotation);
		float lat=(float)Spmath.Radians(latitude);
		return VecMath.mult(this.radius, VecMath.add(VecMath.add(VecMath.mult(Spmath.sinf(lat), Pole), VecMath.mult(Spmath.cosf(lat)*Spmath.cosf(longp), PrMer0)), VecMath.mult(Spmath.cosf(lat)*Spmath.sinf(longp), East)));
	}
	
	//Ecliptic Position of Moon's Local Region from Ground (Update Needed)
	//Parameter: PosLocalM Result
	public IValRef<EVector> posLocalG(IValRef<EVector> p){
		return VecMath.add(this.earthPos, p);
	}
	
	@Override
	protected void updateMagnitude(EVector earthFromSun) {
		double dist=Spmath.getD(VecMath.size(this.earthPos));
		double distS=Spmath.getD(VecMath.size(this.sunPos));
		double distE=Spmath.getD(VecMath.size(earthFromSun));
		double LvsSun=this.radius*this.radius*this.getPhase()*distE*distE*this.albedo*1.4/(dist*dist*distS*distS);
		this.currentMag=-26.74-2.5*Math.log10(LvsSun);
		this.brightness = distE*distE*this.albedo/(distS*distS)*10;
	}
	
	//Illumination of Moon's Local Region (Update Needed)
	//Parameter: PosLocalM Result
	public double illumination(EVector p){
		return -Spmath.getD(BOp.div(VecMath.dot(this.sunPos, p), BOp.mult(VecMath.size(this.sunPos), VecMath.size(p))))*this.brightness;
	}
	
	//Phase of the Moon(Update Needed)
	public double getPhase(){
		return (Math.PI-Math.acos(Spmath.getD(BOp.div(VecMath.dot(this.earthPos, this.sunPos), BOp.mult(VecMath.size(this.earthPos), VecMath.size(this.sunPos))))))/Math.PI;
	}
	
	//Time phase for moon
	public double phase_Time(){
		double k=Math.signum(Spmath.getD(VOp.dot(CrossUtil.cross(this.earthPos, this.sunPos), (IValRef)Pole)))
				*(1.0 - getPhase());
		if(k<0) k=k+2;
		return k/2;
	}

	public double getPeriod() {
		return this.a * Math.sqrt(this.a / parent.mass);
	}

}
