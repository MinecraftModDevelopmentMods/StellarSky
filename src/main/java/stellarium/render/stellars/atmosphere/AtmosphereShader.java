package stellarium.render.stellars.atmosphere;

import java.util.EnumMap;

import javax.annotation.Nullable;

import com.google.common.collect.Maps;

import stellarapi.api.lib.math.Spmath;
import stellarapi.api.lib.math.Vector3;
import stellarium.StellarSkyResources;
import stellarium.render.shader.IShaderObject;
import stellarium.render.shader.IUniformField;
import stellarium.render.shader.ShaderHelper;
import stellarium.render.stellars.StellarRI;
import stellarium.render.stellars.access.EnumStellarPass;

public class AtmosphereShader {

	private IUniformField cameraHeight;
	private IUniformField outerRadius, innerRadius;
	private IUniformField nSamples;
	private IUniformField depthToFogFactor;
	private IUniformField extinctionFactor, gScattering, rayleighFactor, mieFactor;

	private float rainStrengthFactor, weatherFactor;

	private IShaderObject atmosphere;

	public void reloadShaders() {
		this.atmosphere = ShaderHelper.getInstance().buildShader("atmospheresingle",
						StellarSkyResources.vertexAtmosphereSingle, StellarSkyResources.fragmentAtmosphereSingle);

		if(this.atmosphere == null)
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
	}

	public void updateWorldInfo(StellarRI info) {		
		float rainStrength = info.world.getRainStrength(info.partialTicks);
		this.rainStrengthFactor = rainStrength > 0.0f? 0.05f + rainStrength * 0.15f : 0.0f;
		this.weatherFactor = (float) (1.0f - Math.sqrt(rainStrength) * 0.8f);
		weatherFactor *= (1.0D - (double)(info.world.getThunderStrength(info.partialTicks) * 8.0F) / 16.0D);
	}

	public IShaderObject bindAtmShader(AtmosphereModel model, EnumStellarPass pass) {
		atmosphere.bindShader();

		cameraHeight.setDouble(model.getHeight());
		outerRadius.setDouble(model.getOuterRadius());
		innerRadius.setDouble(model.getInnerRadius());
		nSamples.setInteger(10);

		depthToFogFactor.setDouble(100.0 * Math.exp(model.getHeight()) * Math.exp(22.5 * this.rainStrengthFactor));

		Vector3 vec = new Vector3(model.getSkyExtRed(), model.getSkyExtGreen(),
				model.getSkyExtBlue());
		extinctionFactor.setVector3(vec.scale(0.9 - 2.0 * Math.log(this.weatherFactor)));
		gScattering.setDouble(-0.9 + this.rainStrengthFactor);

		double mult = 1.2 * 200.0;

		rayleighFactor.setDouble4(
				mult * 3 * model.getSkyColorRed() * model.getSkyDispRed() / 0.56,
				mult * 7 * model.getSkyColorGreen() * model.getSkyDispGreen() / 0.65,
				mult * 30 * model.getSkyColorBlue() * model.getSkyDispBlue(),
				1.0);

		mieFactor.setDouble4(
				mult * 0.04 * (1.0f + 5 * this.rainStrengthFactor) * model.getSkyDispRed(),
				mult * 0.08 * (1.0f + 5 * this.rainStrengthFactor) * model.getSkyDispGreen(),
				mult * 0.12 * (1.0f + 5 * this.rainStrengthFactor) * model.getSkyDispBlue(),
				1.0);

		return this.atmosphere;
	}
}
