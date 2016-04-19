package stellarium.stellars.system;

import javax.vecmath.Vector3d;

import stellarium.stellars.layer.CelestialObject;
import stellarium.util.math.StellarMath;

public abstract class SolarObject extends CelestialObject {
	
	protected Vector3d relativePos = new Vector3d();
	protected Vector3d sunPos = new Vector3d();
	protected Vector3d earthPos = new Vector3d();
	
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
	
	public Vector3d positionTo(SolarObject object) {
		if(this == object)
			return new Vector3d(0.0, 0.0, 0.0);
		try {
			if(object.level < this.level) {
				Vector3d vector = parent.positionTo(object);
				vector.add(this.relativePos);
				return vector;
			} else {
				Vector3d vector = this.positionTo(object.parent);
				vector.sub(object.relativePos);
				return vector;
			}
		} catch(NullPointerException exception) {
			throw new IllegalArgumentException(String.format(
					"Tried to compare position between non-related objects: %s and %s!",
					this, object));
		}
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
	
	protected void updateMagnitude(Vector3d earthFromSun){
		double dist=this.earthPos.length();
		double distS=this.sunPos.length();
		double distE=earthFromSun.length();
		double LvsSun=this.radius*this.radius*this.getPhase()*distE*distE*this.albedo*1.4/(dist*dist*distS*distS);
		this.currentMag=-26.74-2.5*Math.log10(LvsSun);
	}
	
	public double getPhase(){
		return (1+sunPos.dot(this.earthPos)/(sunPos.length()*this.earthPos.length()))/2;
	}
	
	public abstract Vector3d getRelativePos(double year);


}
