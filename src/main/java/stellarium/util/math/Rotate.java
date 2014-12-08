package stellarium.util.math;

import sciapi.api.value.IValRef;
import sciapi.api.value.STempRef;
import sciapi.api.value.euclidian.ERotate;
import sciapi.api.value.euclidian.EVector;
import sciapi.api.value.euclidian.EVectorSet;
import sciapi.api.value.euclidian.IEVector;
import sciapi.api.value.euclidian.ITransformation;
import sciapi.api.value.matrix.IMatrix;
import sciapi.api.value.numerics.DDouble;
import sciapi.api.value.util.BOp;
import sciapi.api.value.util.VOp;


//Rotation class
public class Rotate extends ERotate {
	
	protected byte sig;
	protected DDouble c, s;
	
	//Rotate creator: b='X' or 'Y' or 'Z', angle: Rotation angle
	public Rotate(char b){
		super(EVectorSet.ins(3).units[1], EVectorSet.ins(3).units[2], true);

		switch(b){
		case 'X':
			this.a = EVectorSet.ins(3).units[1];
			this.b = EVectorSet.ins(3).units[2];
			break;
		case 'Y':
			this.a = EVectorSet.ins(3).units[2];
			this.b = EVectorSet.ins(3).units[0];
			break;
		case 'Z':
			this.a = EVectorSet.ins(3).units[0];
			this.b = EVectorSet.ins(3).units[1];
			break;
		}
		
		sig=(byte)b;
		
		c = new DDouble();
		s = new DDouble();
	}
	
	public Rotate setRAngle(double angle)
	{
		this.setAngle(angle);
		
		c.set(Math.cos(angle));
		s.set(Math.sin(angle));
		
		return this;
	}

	@Override
	public <V extends IEVector> IValRef<V> transform(IValRef<V> v) {
		STempRef<V> ret = v.getParentSet().getSTemp();
		
		switch(sig){
		case 'X':
			VOp.getCoord(ret, 0).set(VOp.getCoord(v, 0));
			VOp.getCoord(ret, 1).set(BOp.sub(BOp.mult(VOp.getCoord(v, 1), (IValRef)c),
					BOp.mult(VOp.getCoord(v, 2), (IValRef)s)));
			VOp.getCoord(ret, 2).set(BOp.add(BOp.mult(VOp.getCoord(v, 1), (IValRef)s),
					BOp.mult(VOp.getCoord(v, 2), (IValRef)c)));
			break;
		case 'Y':
			VOp.getCoord(ret, 1).set(VOp.getCoord(v, 1));
			VOp.getCoord(ret, 2).set(BOp.sub(BOp.mult(VOp.getCoord(v, 2), (IValRef)c),
					BOp.mult(VOp.getCoord(v, 0), (IValRef)s)));
			VOp.getCoord(ret, 0).set(BOp.add(BOp.mult(VOp.getCoord(v, 2), (IValRef)s),
					BOp.mult(VOp.getCoord(v, 0), (IValRef)c)));
			break;
		case 'Z':
			VOp.getCoord(ret, 2).set(VOp.getCoord(v, 2));
			VOp.getCoord(ret, 0).set(BOp.sub(BOp.mult(VOp.getCoord(v, 0), (IValRef)c),
					BOp.mult(VOp.getCoord(v, 1), (IValRef)s)));
			VOp.getCoord(ret, 1).set(BOp.add(BOp.mult(VOp.getCoord(v, 0), (IValRef)s),
					BOp.mult(VOp.getCoord(v, 1), (IValRef)c)));
			break;
		}
		
		v.onUsed();
		
		return ret;
	}
}
