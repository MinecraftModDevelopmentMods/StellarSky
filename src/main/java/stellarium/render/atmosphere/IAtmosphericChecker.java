package stellarium.render.atmosphere;

import stellarapi.api.lib.math.SpCoord;

public interface IAtmosphericChecker {
	
	public void startDescription();
	
	public void pos(SpCoord pos);
	public void brightness(float red, float green, float blue);
	
	/**
	 * Checks if this object is a dominator, and end this description;
	 * */
	public boolean endCheckDominator();
	
	/**
	 * Checks if this object will be rendered.
	 * */
	public boolean endCheckRendered();

}
