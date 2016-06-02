package stellarium.render.stellars.access;

import stellarapi.api.lib.math.SpCoord;

/**
 * Checker to check scatters.
 * */
public interface IStellarChecker {
	
	public void startDescription();
	
	public void pos(SpCoord pos);
	
	/**
	 * Provides radius to approximate the size for opaque sources.
	 * */
	public void radius(float radius);
	
	/**
	 * For dominator check, this will be relative brightness to the sun.
	 * For render check, this will be relative brightness to the magnitude 0.
	 * Note that this will be absolute brightness regardless of its position,
	 * and this will be surface brightness for non-opaque broad sources.
	 * */
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
