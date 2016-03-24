package stellarium.stellars;

import sciapi.api.value.IValRef;
import sciapi.api.value.euclidian.EVector;
import stellarium.util.math.VecMath;

public class Sun extends StellarObj{

	public double radius;
	public double mass;

	//Update Sun
	@Override
	public void update() {
		super.update();
	}

	//Get Direction Vector of Sun from Earth
	@Override
	public synchronized IValRef<EVector> getPosition() {
		IValRef pvec=(IValRef)VecMath.mult(-1.0, getManager().Earth.EcRPos);
		
		return getManager().transforms.projection.transform(pvec);
	}

	@Override
	public void initialize(StellarManager manager) {
		super.initialize(manager);
	}

}
