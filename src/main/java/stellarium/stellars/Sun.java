package stellarium.stellars;

import sciapi.api.value.IValRef;
import sciapi.api.value.euclidian.EVector;
import stellarium.StellarSky;
import stellarium.util.math.Spmath;
import stellarium.util.math.Transforms;
import stellarium.util.math.VecMath;

public class Sun extends StellarObj{

	//Update Sun
	@Override
	public void update() {
		super.update();
	}

	//Get Direction Vector of Sun from Earth
	@Override
	public synchronized IValRef<EVector> getPosition() {
		IValRef pvec=(IValRef)VecMath.mult(-1.0, StellarSky.getManager().Earth.EcRPos);
		
		pvec=Transforms.ZTEctoNEc.transform(pvec);
		pvec=Transforms.EctoEq.transform(pvec);
		pvec=Transforms.NEqtoREq.transform(pvec);
		pvec=Transforms.REqtoHor.transform(pvec);
		
		return pvec;
	}

	@Override
	public void initialize() {
		
	}

}
