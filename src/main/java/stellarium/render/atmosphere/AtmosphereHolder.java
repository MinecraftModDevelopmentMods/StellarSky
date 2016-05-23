package stellarium.render.atmosphere;

import stellarium.StellarSkyResources;
import stellarium.render.shader.IShaderObject;
import stellarium.render.shader.IUniformField;
import stellarium.render.shader.ShaderHelper;

public class AtmosphereHolder {
	
    private IShaderObject atmosphere;
	
    private IUniformField lightDir, cameraHeight;
    private IUniformField outerRadius, innerRadius;
    private IUniformField nSamples;
    private IUniformField exposure;
    private IUniformField depthToFogFactor;
    private IUniformField extinctionFactor, gScattering, rayleighFactor, mieFactor;
	
	public AtmosphereHolder(IShaderObject atmosphere) {
		this.atmosphere = atmosphere;

		this.lightDir = atmosphere.getField("v3LightDir");
		this.cameraHeight = atmosphere.getField("cameraHeight");
		this.outerRadius = atmosphere.getField("outerRadius");
		this.innerRadius = atmosphere.getField("innerRadius");
		this.nSamples = atmosphere.getField("nSamples");

		this.exposure = atmosphere.getField("exposure");
		this.depthToFogFactor = atmosphere.getField("depthToFogFactor");

		this.extinctionFactor = atmosphere.getField("extinctionFactor");
		this.gScattering = atmosphere.getField("g");
		this.rayleighFactor = atmosphere.getField("rayleighFactor");
		this.mieFactor = atmosphere.getField("mieFactor");
	}
}
