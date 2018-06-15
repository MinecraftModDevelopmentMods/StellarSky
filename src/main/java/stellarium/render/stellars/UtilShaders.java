package stellarium.render.stellars;

import stellarium.StellarSkyResources;
import stellarium.render.shader.IShaderObject;
import stellarium.render.shader.ShaderHelper;

public class UtilShaders {
	private IShaderObject point = null, texture = null;

	public void reloadShaders() {
		this.point = ShaderHelper.getInstance().buildShader(
				"point", StellarSkyResources.vertexPoint, StellarSkyResources.fragmentPoint);		
		this.texture = ShaderHelper.getInstance().buildShader("texture",
				StellarSkyResources.vertexTexured, StellarSkyResources.fragmentTextured);
		texture.getField("texture").setInteger(0);

		if(this.point == null || this.texture == null)
			throw new RuntimeException("There was an error preparing shader programs");
	}

	public void bindPointShader() {
		point.bindShader();
	}

	public void bindTextureShader() {
		texture.bindShader();
	}

	public void releaseShader() {
		ShaderHelper.getInstance().releaseCurrentShader();
	}
}
