package stellarium.viewrender.scope;

import stellarium.util.math.Spmath;

public class ScopeEye extends Scope {
	//Default FOV
	public static final double DefaultFOV = 70.0;
	
	//Eye Resolution
	public static final double EyeRes = 0.03;
	
	public void SetScope(double diameter, double magp){
		Conc=diameter*diameter/(eyediameter*eyediameter);
		FOV=DefaultFOV/magp;
		Res=Math.max(Spmath.Degrees(1.22*wavelength/diameter) , EyeRes/magp);
	}
}
