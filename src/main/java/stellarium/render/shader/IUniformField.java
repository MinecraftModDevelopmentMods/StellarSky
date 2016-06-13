package stellarium.render.shader;

import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Vector3;

/**
 * The uniform field for the shader.
 * Use after the shader object is binded.
 * */
public interface IUniformField {

	public void setInteger(int val);
	public void setDouble(double val);
	public void setSpCoord(SpCoord val);
	public void setVector3(Vector3 val);
	public void setDouble3(double x, double y, double z);
	public void setDouble4(double red, double green, double blue, double alpha);
	
}
