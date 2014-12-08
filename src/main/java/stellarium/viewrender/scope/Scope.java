package stellarium.viewrender.scope;

import stellarium.util.math.Spmath;

public class Scope {
	//Wavelength of visible light (meter) 
	public static final double wavelength = 5e-7;
	
	//Eye's Diameter (meter)
	public static final double eyediameter = 0.05;


	//Light Concentration
	public double Conc;
	
	//Field of View
	public double FOV;
	
	// Resolution (Degrees)
	public double Res;
	
	//Adaptive Optics
	public boolean AO=false;
	
	public void AdaptiveOptics(){
		AO=true;
	}
}
