package stellarium.stellars;

import sciapi.api.value.IValRef;
import sciapi.api.value.euclidian.EVector;
import sciapi.api.value.euclidian.IEVector;
import sciapi.api.value.numerics.DDouble;
import sciapi.api.value.util.BOp;
import stellarium.util.math.Spmath;
import stellarium.util.math.Transforms;
import stellarium.util.math.VecMath;

public abstract class SolarObj extends StellarObj {
	
	//Object's Ecliptic Position EVectortor from Earth's center
	EVector EcRPosE = new EVector(3);
	
	//Albedo
	public double Albedo;

	public DDouble Radius = new DDouble();
	
	//Position from Sun
	abstract public IValRef<EVector> GetEcRPos(double time);
	
	//Direction from Earth (Use this after both Earth and Object is Updated) (Do not use this on Earth)
	public IValRef<EVector> GetEcDir(){
		return VecMath.normalize(EcRPosE);
	}

	//Get Direction EVectortor of Object
	public synchronized IValRef<EVector> GetPosition(){
		IValRef pEVector = Transforms.ZTEctoNEc.transform((IEVector)EcRPosE);
		pEVector=Transforms.EctoEq.transform(pEVector);
		pEVector=Transforms.NEqtoREq.transform(pEVector);
		pEVector=Transforms.REqtoHor.transform(pEVector);
		return pEVector;
	}
	
	//Update SolarObj
	@Override
	public void Update(){
		EcRPos.set(GetEcRPos(Transforms.time));
		EcRPosE.set(VecMath.sub(this.EcRPos, StellarManager.Earth.EcRPos));
		super.Update();
		
		this.UpdateMagnitude();
	}
	
	//Update magnitude
	public void UpdateMagnitude(){
		double dist=Spmath.getD(VecMath.size(EcRPosE));
		double distS=Spmath.getD(VecMath.size(EcRPos));
		double distE=Spmath.getD(VecMath.size(StellarManager.Earth.EcRPos));
		double LvsSun=this.Radius.asDouble()*this.Radius.asDouble()*this.GetPhase()*distE*distE*Albedo*1.4/(dist*dist*distS*distS);
		this.Mag=-26.74-2.5*Math.log10(LvsSun);
	}
	
	//Phase of the Object(Update Needed)
	public double GetPhase(){
		return (1+Spmath.getD((BOp.div(VecMath.dot(EcRPos, EcRPosE),BOp.mult(VecMath.size(EcRPos),VecMath.size(EcRPosE))))))/2;
	}

	@Override
	public void Initialize(){
		
	}
}
