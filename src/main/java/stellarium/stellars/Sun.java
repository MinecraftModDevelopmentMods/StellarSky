package stellarium.stellars;

import sciapi.api.value.IValRef;
import sciapi.api.value.euclidian.EVector;
import stellarium.util.math.Spmath;
import stellarium.util.math.Transforms;
import stellarium.util.math.VecMath;

public class Sun extends StellarObj{
	
	//Mass of Sun
	public double Mass;
	
	//Radius of Sun
	public double Radius;

	//Update Sun
	@Override
	public void Update() {
		super.Update();
	}

	//Get Direction Vector of Sun from Earth
	@Override
	public synchronized IValRef<EVector> GetPosition() {
		IValRef pvec=(IValRef)VecMath.mult(-1.0, StellarManager.Earth.EcRPos);
		
		pvec=Transforms.ZTEctoNEc.transform(pvec);
		pvec=Transforms.EctoEq.transform(pvec);
		pvec=Transforms.NEqtoREq.transform(pvec);
		pvec=Transforms.REqtoHor.transform(pvec);
		
		return pvec;
	}

	@Override
	public void Initialize() {
		
	}

}
