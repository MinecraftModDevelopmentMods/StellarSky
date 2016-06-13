package stellarium.render.shader;

import org.lwjgl.opengl.ARBShaderObjects;

import stellarapi.api.lib.math.Vector3;

public interface IShaderObject {

	public void bindShader();
	public void releaseShader();

	/**
	 * Gets uniform field for certain name in this shader object.
	 * @param fieldName the name of the uniform field
	 * */
	public IUniformField getField(String fieldName);

}
