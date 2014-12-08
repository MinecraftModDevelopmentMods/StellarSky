package stellarium.viewrender.render;

import sciapi.api.value.euclidian.EVector;

public abstract class RHost{
	//Direction Vector of Zenith, North, East
	public EVector Zen, North, East;
	
	//Distance per Radius
	public double DtoR;
	
	//Astronomical Seeing of Host Planet
	public double Seeing;
	
	public RHost SetHor(EVector zen, EVector north, EVector east){
		Zen=zen;
		North=north;
		East=east;
		return this;
	}
	
	public RHost SetDtoR(double dtor){
		dtor=DtoR;
		return this;
	}
	
	public RHost NoAtm(){
		Seeing=0.0;
		return this;
	}
	
	public RHost SetSeeing(double seeing){
		Seeing=seeing;
		return this;
	}
	
	abstract public void render();
	
}
