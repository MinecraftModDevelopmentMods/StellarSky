package stellarium.render.atmosphere;

public interface IAtmRenderedObjects {
	
	public void check(ViewerInfo info, IAtmosphericChecker checker);
	
	/**
	 * Rendering opaque object without texture means that
	 * its rendering scatters from the object.
	 * */
	public void render(IAtmosphericTessellator tessellator, boolean forOpaque, boolean hasTexture);

}
