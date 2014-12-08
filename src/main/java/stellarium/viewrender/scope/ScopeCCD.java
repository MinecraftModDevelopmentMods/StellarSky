package stellarium.viewrender.scope;

import stellarium.util.math.Spmath;

public class ScopeCCD extends Scope{

	public void SetScope(double diameter, double focus, double ccdsize){
		Conc=diameter*diameter/(eyediameter*eyediameter);
		FOV=Spmath.Degrees(ccdsize/focus);
		Res=Spmath.Degrees(1.22*wavelength/diameter);
	}
}
