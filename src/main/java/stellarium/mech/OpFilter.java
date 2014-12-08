package stellarium.mech;

import java.awt.Color;

public class OpFilter {

	/**
	 * true for RGB Filter, which is same as eye
	 * RGB filter will ignore wavelength and color.
	 * */
	public boolean isRGB;
	
	/**Filtering Wavelength*/
	public Wavelength wl;
	
	/**Output Color*/
	public Color color;
}
