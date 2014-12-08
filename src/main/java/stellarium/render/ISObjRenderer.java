package stellarium.render;

import stellarium.mech.OpFilter;
import stellarium.objs.IStellarObj;

public interface ISObjRenderer {
	
	/**Renders the Stellar Object by certain Wavelength*/
	public void render(IStellarObj obj, double radVsRes, double brightness, OpFilter filter);
}
