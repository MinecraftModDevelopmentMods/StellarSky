package stellarium.render.stellars;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import stellarapi.api.lib.math.Spmath;
import stellarapi.api.lib.math.Vector3;
import stellarium.render.shader.IShaderObject;
import stellarium.render.stellars.access.EnumStellarPass;
import stellarium.stellars.OpticsHelper;
import stellarium.view.ViewerInfo;

public class CRenderHelper {
	private IShaderObject shader;
	private int renderDominateList;

	public static final double DEEP_DEPTH = 100.0;

	private StellarRI info;

	public void initialize(StellarRI info) {
		this.info = info;
	}

	public void initializePass(EnumStellarPass pass) {
		this.shader = info.get();
		this.renderDominateList = info.getAtmCallList();
	}

	public void renderDominate(Vector3 lightDir, float red, float green, float blue) {
		if(this.shader != null) { // Dummy check for debug
			shader.getField("lightDir").setDouble3(
					lightDir.getX(), lightDir.getY(), lightDir.getZ());
			shader.getField("lightColor").setDouble3(
					red, green, blue);
		}
		GL11.glCallList(this.renderDominateList);
	}
}
