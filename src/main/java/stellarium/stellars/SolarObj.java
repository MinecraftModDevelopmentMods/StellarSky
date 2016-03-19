package stellarium.stellars;

import sciapi.api.value.IValRef;
import sciapi.api.value.euclidian.EVector;
import sciapi.api.value.euclidian.IEVector;
import sciapi.api.value.numerics.DDouble;
import sciapi.api.value.util.BOp;
import stellarium.StellarSky;
import stellarium.util.math.Spmath;
import stellarium.util.math.VecMath;

public abstract class SolarObj extends StellarObj {
	
	//Object's Ecliptic Position EVectortor from Earth's center
	EVector EcRPosE = new EVector(3);
	
	//Albedo
	public double albedo;

	public DDouble radius = new DDouble();
	
	//Position from Sun
	abstract public IValRef<EVector> getEcRPos(double time);
	
	private StellarManager manager;
	
	//Initialize the Object
	public void initialize(StellarManager manager) {
		this.manager = manager;
	}
	
	public StellarManager getManager() {
		return this.manager;
	}
	
	//Direction from Earth (Use this after both Earth and Object is Updated) (Do not use this on Earth)
	public IValRef<EVector> GetEcDir(){
		return VecMath.normalize(EcRPosE);
	}

	//Get Direction EVectortor of Object
	public synchronized IValRef<EVector> getPosition(){
		return getManager().transforms.projection.transform(EcRPosE);
	}
	
	//Update SolarObj
	@Override
	public void update(){
		EcRPos.set(getEcRPos(getManager().transforms.yr));
		EcRPosE.set(VecMath.sub(this.EcRPos, getManager().Earth.EcRPos));
		super.update();
		
		this.updateMagnitude();
	}
	
	//Update magnitude
	public void updateMagnitude(){
		double dist=Spmath.getD(VecMath.size(EcRPosE));
		double distS=Spmath.getD(VecMath.size(EcRPos));
		double distE=Spmath.getD(VecMath.size(getManager().Earth.EcRPos));
		double LvsSun=this.radius.asDouble()*this.radius.asDouble()*this.getPhase()*distE*distE*albedo*1.4/(dist*dist*distS*distS);
		this.mag=-26.74-2.5*Math.log10(LvsSun);
	}
	
	//Phase of the Object(Update Needed)
	public double getPhase(){
		return (1+Spmath.getD((BOp.div(VecMath.dot(EcRPos, EcRPosE),BOp.mult(VecMath.size(EcRPos),VecMath.size(EcRPosE))))))/2;
	}
}
