package stellarium.stellars;

import sciapi.api.value.IValRef;
import sciapi.api.value.euclidian.EVector;
import stellarium.util.math.VecMath;

public class OldEarth extends OldPlanet {
	
	final double MvsE=0.0121505;
	
	//Get Ecliptic Position EVectortor from Sun
	
	public IValRef<EVector> getEcRPos(double yr) {
		this.satellites.get(0).EcRPosE.set(((OldMoon)this.satellites.get(0)).getEcRPosE(yr));
		return VecMath.sub(super.getEcRPos(yr), VecMath.mult(MvsE, this.satellites.get(0).EcRPosE));
	}
	
	//Update Earth(Have to be first)
	public void update(){
		EcRPos.set(getEcRPos(getManager().transforms.yr));
		this.satellites.get(0).update();
	}
	
}
