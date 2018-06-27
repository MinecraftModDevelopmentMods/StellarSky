package stellarium.render.stellars.layer;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import stellarapi.api.lib.math.Matrix3;
import stellarapi.api.lib.math.Vector3;
import stellarium.render.stellars.StellarRI;
import stellarium.render.stellars.UtilShaders;
import stellarium.render.stellars.access.IDominateRenderer;
import stellarium.render.util.BufferBuilderEx;
import stellarium.render.util.FloatVertexFormats;
import stellarium.render.util.TessellatorEx;

public class LayerRHelper {
	public static final double DEEP_DEPTH = 100.0;

	public final Minecraft minecraft;
	public final WorldClient world;
	public final float partialTicks;

	public final TessellatorEx tessellator;
	public final BufferBuilderEx builder;

	private final UtilShaders shaders;

	private final Vector3 xAxis, yAxis;
	private final double pointArea;

	private IDominateRenderer dominater;

	public LayerRHelper(StellarRI info, UtilShaders shaders) {
		this.minecraft = info.minecraft;
		this.world = info.world;
		this.partialTicks = info.partialTicks;

		this.tessellator = TessellatorEx.getInstance();
		this.builder = tessellator.getBuffer();

		this.shaders = shaders;

		Entity viewer = minecraft.getRenderViewEntity();
		Matrix3 transformer = new Matrix3().setAsRotation(0.0, 0.0, 1.0, Math.toRadians(270.0 - viewer.rotationYaw))
				.postMult(new Matrix3().setAsRotation(0.0, 1.0, 0.0, Math.toRadians(-viewer.rotationPitch)));

		this.xAxis = transformer.transform(new Vector3(0.0, info.relativeWidth / minecraft.displayWidth, 0.0));
		this.yAxis = transformer.transform(new Vector3(0.0, 0.0, info.relativeHeight / minecraft.displayHeight));

		// Point sources look darker on smaller screen size because it's 'blurred' as it's picked up by each pixel
		this.pointArea = info.relativeWidth * info.relativeHeight / minecraft.displayWidth / minecraft.displayHeight;
	}


	public void bindTexShader() {
		shaders.bindTextureShader();
	}

	public void unbindTexShader() {
		shaders.releaseShader();
	}

	public void bindTexture(ResourceLocation location) {
		minecraft.getTextureManager().bindTexture(location);
	}


	public void beginPoint() {
		builder.begin(GL11.GL_QUADS, FloatVertexFormats.POSITION_TEX_COLOR_F);
		shaders.bindPointShader();
	}

	public void endPoint() {
		tessellator.draw();
		shaders.releaseShader();
	}

	public void renderPoint(Vector3 pos, float red, float green, float blue) {
		double length = pos.size();
		builder.pos(
				pos.getX() + (xAxis.getX() - yAxis.getX()) * length,
				pos.getY() + (xAxis.getY() - yAxis.getY()) * length,
				pos.getZ() + (xAxis.getZ() - yAxis.getZ()) * length);
		builder.tex(1.0, 0.0).color(red, green, blue, 1.0f).endVertex();
		builder.pos(
				pos.getX() + (xAxis.getX() + yAxis.getX()) * length,
				pos.getY() + (xAxis.getY() + yAxis.getY()) * length,
				pos.getZ() + (xAxis.getZ() + yAxis.getZ()) * length);
		builder.tex(1.0, 1.0).color(red, green, blue, 1.0f).endVertex();
		builder.pos(
				pos.getX() + (- xAxis.getX() + yAxis.getX()) * length,
				pos.getY() + (- xAxis.getY() + yAxis.getY()) * length,
				pos.getZ() + (- xAxis.getZ() + yAxis.getZ()) * length);
		builder.tex(0.0, 1.0).color(red, green, blue, 1.0f).endVertex();
		builder.pos(
				pos.getX() + (- xAxis.getX() - yAxis.getX()) * length,
				pos.getY() + (- xAxis.getY() - yAxis.getY()) * length,
				pos.getZ() + (- xAxis.getZ() - yAxis.getZ()) * length);
		builder.tex(0.0, 0.0).color(red, green, blue, 1.0f).endVertex();
	}

	/** Area of a point in (rad)^2 */
	public double pointArea() {
		return this.pointArea;
	}


	public void apply(StellarRI info) {
		this.dominater = info.getDominateRenderer();
	}

	public void renderDominate(Vector3 lightDir, float red, float green, float blue) {
		dominater.renderDominate(lightDir, red, green, blue);
	}
}
