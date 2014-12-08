package stellarium.viewrender.render;

import sciapi.api.value.IValRef;
import sciapi.api.value.euclidian.EVector;
import stellarium.util.math.*;

public abstract class RBase {
	
	public StellarRenders sr;
	public EVector Pos;
	public double Lum;
	
	public RBase SetPos(IValRef<EVector> pos){
		Pos.set(pos);
		return this;
	}
	
	public RBase SetLum(double lum){
		Lum=lum;
		return this;
	}
	
	public abstract void render();
}
