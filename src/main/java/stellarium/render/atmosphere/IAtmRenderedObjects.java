package stellarium.render.atmosphere;

import stellarium.render.stellars.access.IAtmosphericChecker;
import stellarium.render.stellars.access.IStellarTessellator;
import stellarium.view.ViewerInfo;

public interface IAtmRenderedObjects {
	
	public void check(ViewerInfo info, IAtmosphericChecker checker);
	
	/**
	 * Rendering opaque object without texture means that
	 * its rendering scatters from the object.
	 * */
	public void render(IStellarTessellator tessellator, boolean forOpaque, boolean hasTexture);

}
