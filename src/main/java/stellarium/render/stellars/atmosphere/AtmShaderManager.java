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
	private boolean isFrameBufferEnabled;
	
	private EnumMap<EnumStellarPass, SwitchableShaders> shaderMap = Maps.newEnumMap(EnumStellarPass.class);

	public void reloadShaders() {
		shaderMap.clear();
		
		SwitchableShaders atmosphere = this.injectAtmosphereHook(
				new SwitchableShaders(
						ShaderHelper.getInstance().buildShader("atmospherespcoord", StellarSkyResources.vertexAtmosphereSpCoord, StellarSkyResources.fragmentAtmosphereSpCoord),
						ShaderHelper.getInstance().buildShader("atmospheresimple", StellarSkyResources.vertexAtmosphereSimple, StellarSkyResources.fragmentAtmosphereSimple)));
		
		SwitchableShaders scatterFuzzy = new SwitchableShaders(
				ShaderHelper.getInstance().buildShader("fuzzymapped", StellarSkyResources.vertexScatterFuzzyMapped, StellarSkyResources.fragmentScatterFuzzyMapped),
				ShaderHelper.getInstance().buildShader("fuzzyskylight", StellarSkyResources.vertexScatterFuzzySkylight, StellarSkyResources.fragmentScatterFuzzySkylight));

		SwitchableShaders scatterPoint = new SwitchableShaders(
				ShaderHelper.getInstance().buildShader("pointmapped", StellarSkyResources.vertexScatterPointMapped, StellarSkyResources.fragmentScatterPointMapped),
				ShaderHelper.getInstance().buildShader("pointskylight", StellarSkyResources.vertexScatterPointSkylight, StellarSkyResources.fragmentScatterPointSkylight));

		SwitchableShaders srcTexture = new SwitchableShaders(
				ShaderHelper.getInstance().buildShader("srctexturemapped", StellarSkyResources.vertexTextureMapped, StellarSkyResources.fragmentTextureMapped),
				ShaderHelper.getInstance().buildShader("srctextureskylight", StellarSkyResources.vertexTextureSkylight, StellarSkyResources.fragmentTextureSkylight));


		shaderMap.put(EnumStellarPass.DominateScatter, atmosphere);
		shaderMap.put(EnumStellarPass.SurfaceScatter, srcTexture);
		shaderMap.put(EnumStellarPass.PointScatter, scatterPoint);
		shaderMap.put(EnumStellarPass.Opaque, srcTexture);
		shaderMap.put(EnumStellarPass.OpaqueScatter, scatterFuzzy);
	}

	private SwitchableShaders injectAtmosphereHook(SwitchableShaders shader) {
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
		this.isFrameBufferEnabled = info.isFrameBufferEnabled;

		float rainStrength = info.world.getRainStrength(info.partialTicks);
		this.rainStrengthFactor = rainStrength * 0.2f;
		this.weatherFactor = (float)(1.0D - (double)(rainStrength * 8.0F) / 16.0D);
		weatherFactor *= (1.0D - (double)(info.world.getThunderStrength(info.partialTicks) * 8.0F) / 16.0D);

		this.skyBrightness = info.world.getSunBrightnessFactor(info.partialTicks) * 2.0f;
		this.skyBrightness *= (info.info.brightnessMultiplier / Spmath.sqr(info.info.multiplyingPower));

		this.dominationScale = 0.7;
	}
	
	public IShaderObject bindShader(AtmosphereModel model, EnumStellarPass pass) {
		SwitchableShaders toBind = shaderMap.get(pass);
		this.setupShader(toBind, pass, model);
		return toBind;
	}
	
	private static final String dominationMapField = "skyDominationMap";
	private static final String skyBrightnessField = "skyBrightness";
	private static final String defaultTexture = "texture";
	private static final String dominationScaleField = "dominationscale";

	private void setupShader(SwitchableShaders shader, EnumStellarPass pass, AtmosphereModel model) {
		if(pass != EnumStellarPass.DominateScatter) {
			if(this.isFrameBufferEnabled) {
				shader.switchShader(0);
				shader.bindShader();
				shader.getCurrent().getField(dominationMapField).setInteger(1);
			} else {
				shader.switchShader(1);
				shader.bindShader();
				shader.getCurrent().getField(skyBrightnessField).setDouble(this.skyBrightness);
			}

			if(pass.hasTexture)
				shader.getField(defaultTexture).setInteger(0);

			shader.getField(dominationScaleField).setDouble(this.dominationScale);
		} else {
			if(this.isFrameBufferEnabled) {
				shader.switchShader(0);
			} else {
				shader.switchShader(1);
			}
			
			shader.bindShader();
			
			cameraHeight.setDouble(model.getHeight());
			outerRadius.setDouble(model.getOuterRadius());
			innerRadius.setDouble(model.getInnerRadius());
			nSamples.setInteger(20);

			exposure.setDouble(2.0);
			depthToFogFactor.setDouble(100.0 * Math.exp(model.getHeight()) * Math.exp(-20.0 * this.rainStrengthFactor));

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
