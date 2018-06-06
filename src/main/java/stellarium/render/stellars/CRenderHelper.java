package stellarium.render.stellars;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.util.ResourceLocation;
import stellarapi.api.lib.math.Spmath;
import stellarapi.api.lib.math.Vector3;
import stellarium.render.shader.IShaderObject;
import stellarium.render.stellars.access.EnumStellarPass;
import stellarium.stellars.OpticsHelper;
import stellarium.view.ViewerInfo;

public class CRenderHelper {

	//Limits size per draw call
	private static final int maxBufferSize = 0x80000;


	private float radius;

	private float posX, posY, posZ;
	private float uCoord, vCoord;
	private float red, green, blue;
	private float normalX, normalY, normalZ;

	private float renderedRed, renderedGreen, renderedBlue;


	private boolean renderingDominate, hasTexture;
	private IShaderObject shader;
	private ViewerInfo viewer;
	private int renderDominateList;

	private float rasterizedAngleRatio;

	private float weatherFactor;

	public static final double DEEP_DEPTH = 100.0;

	private StellarRI info;

	public void initialize(StellarRI info) {
		this.weatherFactor = 1.0f - (1.0f - 5.0e-5f) * info.world.getRainStrength(info.partialTicks);

		this.info = info;
		this.viewer = info.info;
		this.rasterizedAngleRatio = (float) (viewer.multiplyingPower / Spmath.Radians(70.0f) * info.screenSize);

		this.calculateColorMult();
	}

	public void initializePass(EnumStellarPass pass) {
		this.renderingDominate = pass.isDominate;
		this.hasTexture = pass.hasTexture;
		this.shader = info.get();
		this.renderDominateList = info.getAtmCallList();

		this.onPreRender();
	}


	public void setup() {
		this.recalcColorInfoObj(true);
	}

	public void radius(float angularRadius) {
		if(!this.hasTexture) {
			this.radius = angularRadius;
			this.recalcColorInfoObj(false);
		}
	}


	private static final float DEFAULT_SIZE = Spmath.Radians(0.06f);

	private float surfMultRed, surfMultGreen, surfMultBlue;
	private float pointMultRed, pointMultGreen, pointMultBlue;
	private float radiusMultRed, radiusMultGreen, radiusMultBlue;
	private float currentMultRed, currentMultGreen, currentMultBlue;

	private void calculateColorMult() {
		float multiplier = (float) (Spmath.sqr(DEFAULT_SIZE) * OpticsHelper.invSpriteScalef());

		this.surfMultRed = (float) (viewer.colorMultiplier.getX() / Spmath.sqr(viewer.multiplyingPower));
		this.surfMultGreen = (float) (viewer.colorMultiplier.getY() / Spmath.sqr(viewer.multiplyingPower));
		this.surfMultBlue = (float) (viewer.colorMultiplier.getZ() / Spmath.sqr(viewer.multiplyingPower));

		this.radiusMultRed = this.surfMultRed * multiplier;
		this.radiusMultGreen = this.surfMultGreen * multiplier;
		this.radiusMultBlue = this.surfMultBlue * multiplier;

		this.pointMultRed = (float) (this.radiusMultRed / Spmath.sqr(viewer.resolutionColor.getX()));
		this.pointMultGreen = (float) (this.radiusMultGreen / Spmath.sqr(viewer.resolutionColor.getY()));
		this.pointMultBlue = (float) (this.radiusMultBlue / Spmath.sqr(viewer.resolutionColor.getZ()));
	}

	private void onPreRender() {
		if(this.hasTexture || this.renderingDominate) {
			this.currentMultRed = this.surfMultRed;
			this.currentMultGreen = this.surfMultGreen;
			this.currentMultBlue = this.surfMultBlue;
		}
	}

	private void recalcColorInfoObj(boolean isCompletelyPoint) {
		if(!this.hasTexture && !this.renderingDominate) {
			if(isCompletelyPoint) {
				this.currentMultRed = this.pointMultRed;
				this.currentMultGreen = this.pointMultGreen;
				this.currentMultBlue = this.pointMultBlue;
			} else {
				this.currentMultRed = (float) (this.radiusMultRed / Spmath.sqr(viewer.resolutionColor.getX() + this.radius));
				this.currentMultGreen = (float) (this.radiusMultGreen / Spmath.sqr(viewer.resolutionColor.getY() + this.radius));
				this.currentMultBlue = (float) (this.radiusMultBlue / Spmath.sqr(viewer.resolutionColor.getZ() + this.radius));
			}
		}

		if(!this.renderingDominate) {
			this.currentMultRed *= this.weatherFactor;
			this.currentMultGreen *= this.weatherFactor;
			this.currentMultBlue *= this.weatherFactor;
		}
	}

	public float multRed() {
		return this.currentMultRed;
	}

	public float multGreen() {
		return this.currentMultGreen;
	}

	public float multBlue() {
		return this.currentMultBlue;
	}


	public void setupSprite() {
		float size = 10.0f * (float)viewer.resolutionGeneral + 2.0f * this.radius;
		GL11.glPointSize(Math.max(this.rasterizedAngleRatio * size, 10.0f));
		if(this.shader != null) { // Dummy check for debug
			if(this.radius > 0.0f)
				shader.getField("scaledRadius").setDouble(this.radius / size);

			shader.getField("invres2").setDouble4(
					OpticsHelper.invSpriteScale2()*this.toInvRes2(size, (float)viewer.resolutionColor.getX()),
					OpticsHelper.invSpriteScale2()*this.toInvRes2(size, (float)viewer.resolutionColor.getY()),
					OpticsHelper.invSpriteScale2()*this.toInvRes2(size, (float)viewer.resolutionColor.getZ()),
					OpticsHelper.invSpriteScale2()*this.toInvRes2(size, (float)viewer.resolutionGeneral));
		}
	}

	public void renderDominate(Vector3 lightDir, float red, float green, float blue) {
		if(this.shader != null) { // Dummy check for debug
			shader.getField("lightDir").setDouble3(
					lightDir.getX(), lightDir.getY(), lightDir.getZ());
			shader.getField("lightColor").setDouble3(
					this.currentMultRed * red,
					this.currentMultGreen * green,
					this.currentMultBlue * blue);
		}
		GL11.glCallList(this.renderDominateList);
	}

	private float toInvRes2(float size, float resolution) {
		float invres = size / resolution;
		return 0.5f * invres * invres;
	}


	public void bindTexture(ResourceLocation location) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(location);
	}
}
