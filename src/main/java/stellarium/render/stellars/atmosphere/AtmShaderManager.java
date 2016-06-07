package stellarium.render.stellars.atmosphere;

import java.util.EnumMap;

import com.google.common.collect.Maps;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.ForgeHooksClient;
import stellarapi.api.lib.math.Spmath;
import stellarapi.api.lib.math.Vector3;
import stellarium.StellarSkyResources;
import stellarium.render.shader.IShaderObject;
import stellarium.render.shader.IUniformField;
import stellarium.render.shader.ShaderHelper;
import stellarium.render.shader.SwitchableShaders;
import stellarium.render.sky.SkyRenderInformation;
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
		this.rainStrengthFactor = 1.0f + 5.0f * rainStrength;
		this.weatherFactor = (float)(1.0D - (double)(rainStrength * 5.0F) / 16.0D);
		weatherFactor *= (1.0D - (double)(info.world.getWeightedThunderStrength(info.partialTicks) * 5.0F) / 16.0D);

		this.skyBrightness = info.world.getSunBrightnessFactor(info.partialTicks);
		this.skyBrightness *= (info.info.brightnessMultiplier / Spmath.sqr(info.info.multiplyingPower));

		this.dominationScale = 0.8;
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
			
			//cameraHeight.setDouble(model.getHeight());
			cameraHeight.setDouble(0.01);
			outerRadius.setDouble(model.getOuterRadius());
			innerRadius.setDouble(model.getInnerRadius());
			nSamples.setInteger(20);

			exposure.setDouble(2.0);
			depthToFogFactor.setDouble(100.0);

			Vector3 vec = new Vector3(0.09, 0.18, 0.27);
			extinctionFactor.setVector3(vec.scale(1.0));
			gScattering.setDouble(-0.85);

			double mult = 1.0;
			
			rayleighFactor.setDouble4(
					mult * 4 * this.weatherFactor * model.getSkyColorRed() / 0.45,
					mult * 8 * this.weatherFactor * model.getSkyColorGreen() / 0.65,
					mult * 16 * this.weatherFactor * model.getSkyColorBlue(),
					1.0);

			mieFactor.setDouble4(
					mult * 0.1 * this.rainStrengthFactor * this.weatherFactor,
					mult * 0.2 * this.rainStrengthFactor * this.weatherFactor,
					mult * 0.3 * this.rainStrengthFactor * this.weatherFactor,
					1.0);
		}
	}
}
