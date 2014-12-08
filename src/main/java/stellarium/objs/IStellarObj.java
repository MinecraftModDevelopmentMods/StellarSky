package stellarium.objs;

import stellarium.catalog.EnumCatalogType;
import stellarium.mech.Wavelength;
import stellarium.util.math.SpCoord;
import stellarium.view.ViewPoint;

public interface IStellarObj {
	
	/**gives the name of this object*/
	public String getName();
	
	/**gives the position of this object from celestial sphere. (As Ecliptic Coordinate)*/
	public SpCoord getPos(ViewPoint vp, double partime);

	/**
	 * gives the radius for certain wavelength.
	 * @param wl the wavelength in which being observed.
	 * */
	public double getRadius(Wavelength wl);
	
	/**
	 * gives the apparent magnitude for certain wavelength.
	 * @param wl the wavelength in which being observed.
	 * */
	public double getMag(Wavelength wl);
	
	/**gives the render id for this object*/
	public int getRenderId();
	
	/**gives the description for this object*/
	public String getDescription();
	
	/**gives the type of this object*/
	public EnumSObjType getType();
}
