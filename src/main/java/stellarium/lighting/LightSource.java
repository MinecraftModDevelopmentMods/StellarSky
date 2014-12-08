package stellarium.lighting;

import sciapi.api.value.euclidian.EVector;
import sciapi.api.value.numerics.IReal;
import sciapi.api.value.util.BOp;
import sciapi.api.value.util.VOp;


public class LightSource {
	public EVector Pos;
	public double Lum;
	public double Size;
	
	public LightSource(EVector pos, double lum, double size){
		Pos = pos;
		Lum = lum;
		Size = size;
	}
	
	public static LightSource instance;
	
	
	public double GetFlux(double Dist){
		return Lum/(Dist*Dist);
	}
	
	public double GetFlux(EVector gPos){
		return Lum / ((IReal) VOp.size2(BOp.sub(gPos, Pos))).asDouble();
	}
}
