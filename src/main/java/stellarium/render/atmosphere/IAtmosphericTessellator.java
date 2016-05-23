package stellarium.render.atmosphere;

import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Vector3;

public interface IAtmosphericTessellator {
	
	public void begin(boolean hasNormal);
	
	public void pos(SpCoord pos, float depth);
	public void color(float red, float green, float blue);
	/** For textured objects */
	public void texture(float u, float v);
	/** For textured objects */
	public void normal(Vector3 normal);
	public void writeVertex();
	
	/** For opaque scatterings */
	public void radius(float angularRadius);
	
	public void end();

}
