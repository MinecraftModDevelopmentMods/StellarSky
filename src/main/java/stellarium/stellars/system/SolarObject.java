package stellarium.stellars.system;

import sciapi.api.value.euclidian.EVector;
import sciapi.api.value.util.BOp;
import stellarium.stellars.sketch.CelestialObject;
import stellarium.stellars.sketch.IRenderCache;
import stellarium.util.math.Spmath;
import stellarium.util.math.VecMath;

public abstract class SolarObject extends CelestialObject {
	
	protected EVector sunPos = new EVector(3);
	protected EVector earthPos = new EVector(3);
	
	/**Magnitude from earth without atmosphere*/
	protected double currentMag;
	
	/**Albedo*/
	protected double albedo;
	
	/**Radius*/
	protected double radius;

	public SolarObject(boolean isRemote) {
		super(isRemote);
	}

	@Override
	public IRenderCache generateCache() {
		return new PlanetRenderCache();
	}

	@Override
	public int getRenderId() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void update(double year, EVector earthFromSun) {
		sunPos.set(getEcRPos(year));
		earthPos.set(VecMath.sub(this.sunPos, earthFromSun));
		this.updateMagnitude(earthFromSun);
	}
	
	private void updateMagnitude(EVector earthFromSun){
		double dist=Spmath.getD(VecMath.size(earthPos));
		double distS=Spmath.getD(VecMath.size(sunPos));
		double distE=Spmath.getD(VecMath.size(earthFromSun));
		double LvsSun=this.radius*this.radius*this.getPhase()*distE*distE*this.albedo*1.4/(dist*dist*distS*distS);
		this.currentMag=-26.74-2.5*Math.log10(LvsSun);
	}
	
	public double getPhase(){
		return (1+Spmath.getD((BOp.div(VecMath.dot(sunPos, earthPos),BOp.mult(VecMath.size(sunPos),VecMath.size(earthPos))))))/2;
	}
	
	abstract public EVector getEcRPos(double year);


}
