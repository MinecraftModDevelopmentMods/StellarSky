package stellarium.util;

import sciapi.api.value.IValRef;
import sciapi.api.value.euclidian.EVector;
import sciapi.api.value.numerics.DDouble;
import sciapi.api.value.numerics.IReal;
import sciapi.api.value.util.VOp;
import stellarium.util.math.*;

public class DVec {
	public EVector pre, post;
	
	public void Set(EVector v){
		pre=post;
		post=v;
	}
	
	public void Set(IValRef<EVector> v){
		if(pre == null)
			pre = new EVector(3);
		if(post == null)
			post = new EVector(3);
		
		pre.set(post);
		post.set(v);
	}
	
	public EVector Get(){
		return post;
	}
	
	public IValRef<EVector> Get(double part){
		return VecMath.add(VecMath.mult((IReal)new DDouble(1-part), pre), VecMath.mult((IReal)new DDouble(part), post));
	}
}
