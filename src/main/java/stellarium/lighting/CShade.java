package stellarium.lighting;

import stellarium.util.*;
import stellarium.util.math.*;
import stellarium.stellars.cbody.*;

public class CShade {
	public CBody cause;
	
	public DVec ShadePos;
	public UDouble Depth;
	public UDouble EffRad;
	public UDouble size_LS, size_Sh;
	public UDouble d_LS, d_Sh;
	
	public CShade Get(double part){
		CShade sh=new CShade();
		sh.ShadePos.Set(ShadePos.Get(part));
		sh.Depth.Set(Depth.Get(part));
		sh.EffRad.Set(EffRad.Get(part));
		sh.size_LS.Set(size_LS.Get(part));
		sh.size_Sh.Set(size_Sh.Get(part));
		sh.d_LS.Set(d_LS.Get(part));
		sh.d_Sh.Set(d_Sh.Get(part));

		return sh;
	}
}
