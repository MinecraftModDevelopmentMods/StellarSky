package stellarium.render.stellars.atmosphere;

import stellarapi.api.lib.math.Vector3;
import stellarium.StellarSkyResources;
import stellarium.render.shader.IShaderObject;
import stellarium.render.shader.IUniformField;
import stellarium.render.shader.ShaderHelper;
import stellarium.render.stellars.StellarRI;

public class AtmosphereShader {

	private IUniformField cameraHeight;
	private IUniformField outerRadius, innerRadius;
	private IUniformField nSamples;
	private IUniformField depthToFogFactor;
	private IUniformField extinctionFactor, gScattering, rayleighFactor, mieFactor;

	private IUniformField cameraHeight2;
	private IUniformField outerRadius2, innerRadius2;
	private IUniformField extinctionFactor2;

	private IUniformField fieldRelative;

	private float rainStrengthFactor, weatherFactor;
	private double relativeWidth, relativeHeight;

	private IShaderObject atmosphere, extinction, refraction;

	public void reloadShaders() {
		this.atmosphere = ShaderHelper.getInstance().buildShader("atmospheresingle",
						StellarSkyResources.vertexAtmosphereSingle, StellarSkyResources.fragmentAtmosphereSingle);
		this.extinction = ShaderHelper.getInstance().buildShader("atmextinction",
				StellarSkyResources.vertexAtmExtinction, StellarSkyResources.fragmentAtmExtinction);
		this.refraction = ShaderHelper.getInstance().buildShader("atmrefraction",
				StellarSkyResources.vertexAtmRefraction, StellarSkyResources.fragmentAtmRefraction);

		if(this.atmosphere == null || this.extinction == null || this.refraction == null)
			throw new RuntimeException("There was an error preparing shader programs");

		this.cameraHeight = atmosphere.getField("cameraHeight");
		this.outerRadius = atmosphere.getField("outerRadius");
		this.innerRadius = atmosphere.getField("innerRadius");
		this.nSamples = atmosphere.getField("nSamples");

		this.depthToFogFactor = atmosphere.getField("depthToFogFactor");

		this.extinctionFactor = atmosphere.getField("extinctionFactor");
		this.gScattering = atmosphere.getField("g");
		this.rayleighFactor = atmosphere.getField("rayleighFactor");
		this.mieFactor = atmosphere.getField("mieFactor");


		this.cameraHeight2 = extinction.getField("cameraHeight");
		this.outerRadius2 = extinction.getField("outerRadius");
		this.innerRadius2 = extinction.getField("innerRadius");
		this.extinctionFactor2 = extinction.getField("extinctionFactor");


		this.fieldRelative = refraction.getField("relative");
	}

	public void updateWorldInfo(StellarRI info) {		
		float rainStrength = info.world.getRainStrength(info.partialTicks);
		this.rainStrengthFactor = rainStrength > 0.0f? 0.05f + rainStrength * 0.15f : 0.0f;
		this.weatherFactor = (float) (1.0f - Math.sqrt(rainStrength) * 0.8f);
		weatherFactor *= (1.0D - (double)(info.world.getThunderStrength(info.partialTicks) * 8.0F) / 16.0D);

		this.relativeWidth = info.relativeWidth;
		this.relativeHeight = info.relativeHeight;
	}

	public void bindExtinctionShader(AtmosphereModel model) {
		extinction.bindShader();

		cameraHeight2.setDouble(model.getHeight());
		outerRadius2.setDouble(model.getOuterRadius());
		innerRadius2.setDouble(model.getInnerRadius());

		// Extinction in magnitude
		Vector3 vec = new Vector3(model.getSkyExtRed(), model.getSkyExtGreen(), model.getSkyExtBlue());

		extinctionFactor2.setVector3(vec.scale(Math.log(10) * 0.4 - 2.0 * Math.log(this.weatherFactor))); // Inverted?
	}

	public IShaderObject bindRefractionShader(AtmosphereModel model) {
		refraction.bindShader();
		fieldRelative.setDouble3(this.relativeWidth, this.relativeHeight, 1.0);
		return this.refraction;
	}

	public IShaderObject bindAtmShader(AtmosphereModel model) {
		atmosphere.bindShader();

		cameraHeight.setDouble(model.getHeight());
		outerRadius.setDouble(model.getOuterRadius());
		innerRadius.setDouble(model.getInnerRadius());
		nSamples.setInteger(10);

		depthToFogFactor.setDouble(0.1 * Math.exp(-22.5 * this.rainStrengthFactor));

		// Extinction in magnitude
		Vector3 vec = new Vector3(model.getSkyExtRed(), model.getSkyExtGreen(), model.getSkyExtBlue());

		extinctionFactor.setVector3(vec.scale(Math.log(10) * 0.4 - 2.0 * Math.log(this.weatherFactor))); // Inverted?
		gScattering.setDouble(-0.9 + this.rainStrengthFactor);

		double mult = 200.0;

		rayleighFactor.setDouble4(
				mult * 3 * (model.getSkyColorRed() / 0.56) * model.getSkyDispRed(),
				mult * 7 * (model.getSkyColorGreen() / 0.65) * model.getSkyDispGreen(),
				mult * 20 * model.getSkyColorBlue() * model.getSkyDispBlue(),
				1.0);

		mieFactor.setDouble4(
				mult * 0.05 * (1.0f + 5 * this.rainStrengthFactor) * model.getSkyDispRed(),
				mult * 0.05 * (1.0f + 5 * this.rainStrengthFactor) * model.getSkyDispGreen(),
				mult * 0.05 * (1.0f + 5 * this.rainStrengthFactor) * model.getSkyDispBlue(),
				1.0);

		return this.atmosphere;
	}
}
