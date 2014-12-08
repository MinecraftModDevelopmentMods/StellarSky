package stellarium.view;

import stellarium.mech.Wavelength;

public interface IScope {
	
	/**Light Gathering Power of this scope*/
	public double getLGP();

	/**Resolution of this scope on specific wavelength*/
	public double getResolution(Wavelength wl);
	
	/**Magnifying Power of this scope, only available for Eye-type scopes*/
	public double getMP();
	
	/**Plate Scale of this scope, only available for CCD-type scopes*/
	public double getPlateScale();
	
	/**The type of this scope*/
	public EnumEyeCCD getType();
}
