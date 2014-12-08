package stellarium.util;

public class UDouble {
	public double pre, post;
	
	public void Set(double d){
		pre=post;
		post=d;
	}
	
	public double Get(){
		return post;
	}
	
	public double Get(double part){
		return (1.0-part)*pre+part*post;
	}
}
