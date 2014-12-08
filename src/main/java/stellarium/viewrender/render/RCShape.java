package stellarium.viewrender.render;

public abstract class RCShape extends RBase {
	public double Size;
	public double Dist;
	
	public void SetSize(double size){
		Size=size;
	}
	public void SetDist(double dist){
		Dist=dist;
	}
	
	@Override
	public abstract void render();

}
