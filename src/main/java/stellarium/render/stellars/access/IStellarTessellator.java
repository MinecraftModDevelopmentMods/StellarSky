package stellarium.render.stellars.access;

import net.minecraft.util.ResourceLocation;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Vector3;

public interface IStellarTessellator {
	
	public void bindTexture(ResourceLocation location);
	
	public void begin(boolean hasNormal);
	
	public void pos(Vector3 pos, float scale);

	/**
	 * Appends color <b>without</b> scope & filter.
	 * */
	public void color(float red, float green, float blue);
	
	/** For textured objects */
	public void texture(float u, float v);
	
	/** For textured objects */
	public void normal(Vector3 normal);
	
	public void writeVertex();
	
	/**
	 * For opaque scatterings.
	 * Limited to once per call.
	 *  */
	public void radius(float angularRadius);
	
	public void end();


	/**
	 * Internal method
	 * */
	public void initializePass(EnumStellarPass pass);

}
