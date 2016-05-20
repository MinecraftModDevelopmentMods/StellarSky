package stellarium.render.tessellation;

import stellarapi.api.lib.math.Matrix3;

public interface IOverallTessellation {
	
	public int horFragNum();
	public int vertFragNum();
	
	/**
	 * Reference Matrix.
	 * Pre-multiplication of vector (1,0,0) will be the spherical reference point,
	 * and that of (0,0,1) will be the pole.
	 * */
	public Matrix3 getReference();
	
	public int 

}
