package stellarium.render.stellars.atmosphere;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.ForgeHooksClient;
import stellarapi.api.lib.math.Spmath;
import stellarapi.api.lib.math.Vector3;
import stellarium.render.shader.IShaderObject;
import stellarium.render.shader.IUniformField;
import stellarium.render.sky.SkyRenderInformation;

public class AtmShaderManager {

	private IShaderObject atmosphere;

	private IUniformField cameraHeight;
	private IUniformField outerRadius, innerRadius;
	private IUniformField nSamples;
	private IUniformField exposure;
	private IUniformField depthToFogFactor;
	private IUniformField extinctionFactor, gScattering, rayleighFactor, mieFactor;

	private static final float OUTER_RADIUS = 820.0f;
	private static final float INNER_RADIUS = 800.0f;

	private float height;
	private double angle;

	private float rainStrengthFactor, weatherFactor;
	private float skyred, skygreen, skyblue;
	private float skyBrightness;

	public void initialize(IShaderObject shader) {
		this.atmosphere = shader;

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
	}

	// TODO move to model
	public void updateWorldInfo(SkyRenderInformation info) {
		float rainStrength = info.world.getRainStrength(info.partialTicks);
		this.rainStrengthFactor = 1.0f + 5.0f * rainStrength;
		this.weatherFactor = (float)(1.0D - (double)(rainStrength * 5.0F) / 16.0D);
		weatherFactor *= (1.0D - (double)(info.world.getWeightedThunderStrength(info.partialTicks) * 5.0F) / 16.0D);

		float height = 0.1f;
		float groundFactor = 1.0f / (2 * height + 1.0f);

		EntityLivingBase viewer = info.minecraft.renderViewEntity;
		int i = MathHelper.floor_double(viewer.posX);
		int j = MathHelper.floor_double(viewer.posY);
		int k = MathHelper.floor_double(viewer.posZ);
		int l = ForgeHooksClient.getSkyBlendColour(info.world, i, j, k);
		this.skyred = (float)(l >> 16 & 255) / 255.0F;
		this.skygreen = (float)(l >> 8 & 255) / 255.0F;
		this.skyblue = (float)(l & 255) / 255.0F;

		this.height = (float)(viewer.posY - info.world.getHorizon()) / info.world.getHeight();
		this.angle = Spmath.Degrees(Math.acos(1.0 / (this.height / INNER_RADIUS + 1.0)));
	}

	// TODO move to renderer
	public void configureAtmosphere() {
		atmosphere.bindShader();

		cameraHeight.setDouble(this.height);
		outerRadius.setDouble(OUTER_RADIUS);
		innerRadius.setDouble(INNER_RADIUS);
		nSamples.setInteger(20);

		exposure.setDouble(2.0);
		depthToFogFactor.setDouble(10.0);

		Vector3 vec = new Vector3(0.09, 0.18, 0.27);
		extinctionFactor.setVector3(vec.scale(1.0));
		gScattering.setDouble(-0.85);

		rayleighFactor.setDouble4(
				4 * this.weatherFactor * this.skyred / 0.45,
				8 * this.weatherFactor * this.skygreen / 0.65,
				16 * this.weatherFactor * this.skyblue,
				1.0);

		mieFactor.setDouble4(
				0.1 * this.rainStrengthFactor * this.weatherFactor,
				0.2 * this.rainStrengthFactor * this.weatherFactor,
				0.3 * this.rainStrengthFactor * this.weatherFactor,
				1.0);
	}

}
