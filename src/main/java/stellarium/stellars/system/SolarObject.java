package stellarium.stellars.system;

import stellarapi.api.lib.math.Vector3;
import stellarium.stellars.layer.StellarObject;

public abstract class SolarObject extends StellarObject {
	
	private String name;
	
	protected Vector3 relativePos = new Vector3();
	protected Vector3 sunPos = new Vector3();
	protected Vector3 earthPos = new Vector3();
	
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

	public SolarObject(String name) {
		this.name = name;
		this.parent = null;
		this.level = 0;
	}
	
	public SolarObject(String name, SolarObject parent) {
		this.name = name;
		this.parent = parent;
		this.level = parent.level + 1;
	}
	
	public void initialize() { }
	
	@Override
	public String getID() {
		return this.name;
	}
	
	public Vector3 positionTo(SolarObject object) {
		if(this == object)
			return new Vector3(0.0, 0.0, 0.0);
		try {
			if(object.level < this.level) {
				Vector3 vector = parent.positionTo(object);
				vector.add(this.relativePos);
				return vector;
			} else {
				Vector3 vector = this.positionTo(object.parent);
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
	
	protected void updateMagnitude(Vector3 earthFromSun){
		double dist=earthPos.size();
		double distS=sunPos.size();
		double distE=earthFromSun.size();
		double LvsSun=this.radius*this.radius*this.getPhase()*distE*distE*this.albedo*1.4/(dist*dist*distS*distS);
		this.currentMag=-26.74-2.5*Math.log10(LvsSun);
	}
	
	public abstract double absoluteOffset();
	
	public double getPhase(){
		return (1+sunPos.dot(this.earthPos)/(sunPos.size()*this.earthPos.size()))/2;
	}
	
	public double phaseOffset() {
		Vector3 crossed = new Vector3();
		crossed.setCross(this.earthPos, this.sunPos);
		double k=Math.signum(crossed.dot(new Vector3(0.0, 0.0, 1.0))) * (1.0 - getPhase());
		if(k<0) k=k+2;
		return k/2;
	}
	
	public abstract Vector3 getRelativePos(double year);


}
