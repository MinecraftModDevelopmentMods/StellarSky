package stellarium.util;

public class UpDouble {
	public double val0, vald, val;
	public void Init(double v0, double vd){
		val0=v0;
		vald=vd;
	}
	public void Update(double t){
		val=val0+vald*t;
	}
}
