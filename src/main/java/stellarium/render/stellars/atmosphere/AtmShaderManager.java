package stellarium.render.stellars.atmosphere;

import java.util.EnumMap;

import com.google.common.collect.Maps;

import stellarapi.api.lib.math.Spmath;
import stellarapi.api.lib.math.Vector3;
import stellarium.StellarSkyResources;
import stellarium.render.shader.IShaderObject;
import stellarium.render.shader.IUniformField;
import stellarium.render.shader.ShaderHelper;
import stellarium.render.shader.SwitchableShaders;
import stellarium.render.stellars.access.EnumStellarPass;
import stellarium.render.stellars.phased.StellarRenderInformation;

public class AtmShaderManager {

	private IUniformField cameraHeight;
	private IUniformField outerRadius, innerRadius;
	private IUniformField nSamples;
	private IUniformField exposure;
	private IUniformField depthToFogFactor;
	private IUniformField extinctionFactor, gScattering, rayleighFactor, mieFactor;

	private float rainStrengthFactor, weatherFactor;
	private double skyBrightness, dominationScale;
	
	private EnumMap<EnumStellarPass, IShaderObject> shaderMap = Maps.newEnumMap(EnumStellarPass.class);

	public void reloadShaders() {
		shaderMap.clear();
		
		IShaderObject atmosphere = this.injectAtmosphereHook(
				ShaderHelper.getInstance().buildShader("atmospherespcoord", StellarSkyResources.vertexAtmosphereSpCoord, StellarSkyResources.fragmentAtmosphereSpCoord));

		IShaderObject scatterFuzzy = ShaderHelper.getInstance().buildShader(
				"fuzzymapped", StellarSkyResources.vertexScatterFuzzyMapped, StellarSkyResources.fragmentScatterFuzzyMapped);

		IShaderObject scatterPoint = ShaderHelper.getInstance().buildShader(
				"pointmapped", StellarSkyResources.vertexScatterPointMapped, StellarSkyResources.fragmentScatterPointMapped);

		IShaderObject srcTexture = ShaderHelper.getInstance().buildShader(
				"srctexturemapped", StellarSkyResources.vertexTextureMapped, StellarSkyResources.fragmentTextureMapped);


		shaderMap.put(EnumStellarPass.DominateScatter, atmosphere);
		shaderMap.put(EnumStellarPass.SurfaceScatter, srcTexture);
		shaderMap.put(EnumStellarPass.PointScatter, scatterPoint);
		shaderMap.put(EnumStellarPass.Opaque, srcTexture);
		shaderMap.put(EnumStellarPass.OpaqueScatter, scatterFuzzy);
	}

	private IShaderObject injectAtmosphereHook(IShaderObject shader) {
		this.cameraHeight = shader.getField("cameraHeight");
		this.outerRadius = shader.getField("outerRadius");
		this.innerRadius = shader.getField("innerRadius");
		this.nSamples = shader.getField("nSamples");

		this.exposure = shader.getField("exposure");
		this.depthToFogFactor = shader.getField("depthToFogFactor");

		this.extinctionFactor = shader.getField("extinctionFactor");
		this.gScattering = shader.getField("g");
		this.rayleighFactor = shader.getField("rayleighFactor");
		this.mieFactor = shader.getField("mieFactor");
		
		return shader;
	}

	public void updateWorldInfo(StellarRenderInformation info) {		
		float rainStrength = info.world.getRainStrength(info.partialTicks);
		this.rainStrengthFactor = rainStrength > 0.0f? 0.05f + rainStrength * 0.15f : 0.0f;
		this.weatherFactor = (float) (1.0f - Math.sqrt(rainStrength) * 0.8f);
		weatherFactor *= (1.0D - (double)(info.world.getThunderStrength(info.partialTicks) * 8.0F) / 16.0D);

		this.skyBrightness = info.world.getSunBrightnessFactor(info.partialTicks) * 2.0f;
		this.skyBrightness *= (info.info.brightnessMultiplier / Spmath.sqr(info.info.multiplyingPower));

		this.dominationScale = 0.7;
	}
	
	public IShaderObject bindShader(AtmosphereModel model, EnumStellarPass pass) {
		IShaderObject toBind = shaderMap.get(pass);
		this.setupShader(toBind, pass, model);
		return toBind;
	}
	
	private static final String dominationMapField = "skyDominationMap";
	private static final String defaultTexture = "texture";
	private static final String dominationScaleField = "dominationscale";

	private void setupShader(IShaderObject shader, EnumStellarPass pass, AtmosphereModel model) {
		if(pass != EnumStellarPass.DominateScatter) {
			shader.bindShader();
			shader.getField(dominationMapField).setInteger(1);

			if(pass.hasTexture)
				shader.getField(defaultTexture).setInteger(0);

			shader.getField(dominationScaleField).setDouble(this.dominationScale);
		} else {
			shader.bindShader();

			cameraHeight.setDouble(model.getHeight());
			outerRadius.setDouble(model.getOuterRadius());
			innerRadius.setDouble(model.getInnerRadius());
			nSamples.setInteger(20);

			exposure.setDouble(2.0);
			depthToFogFactor.setDouble(100.0 * Math.exp(model.getHeight()) * Math.exp(-22.5 * this.rainStrengthFactor));

			Vector3 vec = new Vector3(model.getSkyExtRed(),
					model.getSkyExtGreen(), model.getSkyExtBlue());
			extinctionFactor.setVector3(vec.scale(0.9 - 2.0 * Math.log(this.weatherFactor)));
			gScattering.setDouble(-0.9 + this.rainStrengthFactor);

			double mult = 1.2;
			
			rayleighFactor.setDouble4(
					mult * 4 * model.getSkyColorRed() * model.getSkyDispRed() / 0.56,
					mult * 8 * model.getSkyColorGreen() * model.getSkyDispGreen() / 0.65,
					mult * 16 * model.getSkyColorBlue() * model.getSkyDispBlue(),
					1.0);

			mieFactor.setDouble4(
					mult * 0.1 * (1.0f + 5 * this.rainStrengthFactor) * model.getSkyDispRed(),
					mult * 0.2 * (1.0f + 5 * this.rainStrengthFactor) * model.getSkyDispGreen(),
					mult * 0.3 * (1.0f + 5 * this.rainStrengthFactor) * model.getSkyDispBlue(),
					1.0);
		}
	}
}
