package stellarium.stellars.system;

import sciapi.api.value.IValRef;
import sciapi.api.value.euclidian.EVector;
import sciapi.api.value.util.BOp;
import stellarium.stellars.sketch.CelestialObject;
import stellarium.util.math.Spmath;
import stellarium.util.math.VecMath;

public abstract class SolarObject extends CelestialObject {
	
	protected EVector relativePos = new EVector(3);
	protected EVector sunPos = new EVector(3);
	protected EVector earthPos = new EVector(3);
	
	/**Magnitude from earth without atmosphere*/
	protected double currentMag;
	
	/**Albedo*/
	protected double albedo;
	
	/**Radius*/
	protected double radius;
	
	/**Mass*/
	protected double mass;
	
	/**Parent*/
	protected final SolarObject parent;
	private final int level;

	public SolarObject(boolean isRemote) {
		super(isRemote);
		this.parent = null;
		this.level = 0;
	}
	
	public SolarObject(boolean isRemote, SolarObject parent) {
		super(isRemote);
		this.parent = parent;
		this.level = parent.level + 1;
	}
	
	public void initialize() { }
	
	public IValRef<EVector> positionTo(SolarObject object) {
		if(this == object)
			return new EVector(0.0, 0.0, 0.0);
		if(object.level < this.level)
			return VecMath.add(this.relativePos, parent.positionTo(object));
		else if(object.level != this.level)
			return VecMath.sub(this.positionTo(object.parent), object.relativePos);
		else throw new IllegalArgumentException("Tried to compare position between non-related objects!");
	}
	
	public void updatePre(double year) {
		if(this.parent != null)
			relativePos.set(this.getRelativePos(year));
	}
	
	public void updateModulate() { }
	
	public void updatePos(SolarObject sun, SolarObject earth) {
		earthPos.set(this.positionTo(earth));
		sunPos.set(this.positionTo(sun));
	}
	
	public void updatePost(SolarObject earth) {
		if(this != earth)
			this.updateMagnitude(earth.sunPos);
	}
	
	private void updateMagnitude(EVector earthFromSun){
		double dist=Spmath.getD(VecMath.size(this.earthPos));
		double distS=Spmath.getD(VecMath.size(this.sunPos));
		double distE=Spmath.getD(VecMath.size(earthFromSun));
		double LvsSun=this.radius*this.radius*this.getPhase()*distE*distE*this.albedo*1.4/(dist*dist*distS*distS);
		this.currentMag=-26.74-2.5*Math.log10(LvsSun);
	}
	
	public double getPhase(){
		return (1+Spmath.getD((BOp.div(VecMath.dot(this.sunPos, this.earthPos),
				BOp.mult(VecMath.size(this.sunPos),VecMath.size(this.earthPos))))))/2;
	}
	
	public abstract EVector getRelativePos(double year);


}
