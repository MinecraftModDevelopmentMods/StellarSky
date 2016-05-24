package stellarium.render.atmosphere;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.ForgeHooksClient;
import stellarapi.api.ISkyEffect;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Spmath;
import stellarapi.api.lib.math.Vector3;
import stellarium.render.shader.IShaderObject;
import stellarium.render.shader.IUniformField;
import stellarium.stellars.Optics;

public class AtmosphereHolder {
	
    private IShaderObject atmosphere;
	
    private IUniformField lightDir, cameraHeight;
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
    
	public AtmosphereHolder(IShaderObject atmosphere) {
		this.atmosphere = atmosphere;

		this.lightDir = atmosphere.getField("lightDir");
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
	
	public void update(Minecraft mc, WorldClient theWorld, float partialTicks) {
		float rainStrength = theWorld.getRainStrength(partialTicks);
		this.rainStrengthFactor = 1.0f + 5.0f * rainStrength;
		this.weatherFactor = (float)(1.0D - (double)(rainStrength * 5.0F) / 16.0D);
        weatherFactor *= (1.0D - (double)(theWorld.getWeightedThunderStrength(partialTicks) * 5.0F) / 16.0D);
		
        float height = 0.1f;
        float groundFactor = 1.0f / (2 * height + 1.0f);
        
        this.skyBrightness = theWorld.getSunBrightnessFactor(partialTicks);
        
        int i = MathHelper.floor_double(mc.renderViewEntity.posX);
        int j = MathHelper.floor_double(mc.renderViewEntity.posY);
        int k = MathHelper.floor_double(mc.renderViewEntity.posZ);
        int l = ForgeHooksClient.getSkyBlendColour(theWorld, i, j, k);
        this.skyred = (float)(l >> 16 & 255) / 255.0F;
        this.skygreen = (float)(l >> 8 & 255) / 255.0F;
        this.skyblue = (float)(l & 255) / 255.0F;

        this.height = (float)(mc.renderViewEntity.posY - theWorld.getHorizon()) / theWorld.getHeight();
		this.angle = Spmath.Degrees(Math.acos(1.0 / (this.height / INNER_RADIUS + 1.0)));
	}

	public void setupShader(SpCoord coord, Vector3 color) {
		atmosphere.bindShader();
		
		lightDir.setVector3(coord.getVec());
		
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
				4 * color.getX() * this.weatherFactor * this.skyred / 0.45,
				8 * color.getY() * this.weatherFactor * this.skygreen / 0.65,
				16 * color.getZ() * this.weatherFactor * this.skyblue,
				1.0);
		
		mieFactor.setDouble4(
				0.1 * color.getX() * this.rainStrengthFactor * this.weatherFactor,
				0.2 * color.getY() * this.rainStrengthFactor * this.weatherFactor,
				0.3 * color.getZ() * this.rainStrengthFactor * this.weatherFactor,
				1.0);
	}

	public float brightnessScale(SpCoord curPos, ISkyEffect sky) {
		double newYAngle = 90.0 * (curPos.y + this.angle) / (90.0 + this.angle);
		float airmass = sky.calculateAirmass(new SpCoord(curPos.x, newYAngle));
		
		return Optics.getAlphaFromMagnitude(airmass, 0.0f);
	}

	public double getSkyBrightness() {
		return this.skyBrightness;
	}
}
