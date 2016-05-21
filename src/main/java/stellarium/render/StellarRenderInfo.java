package stellarium.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;

public class StellarRenderInfo {

	public Minecraft mc;
	public Tessellator tessellator;
	public VertexBuffer worldrenderer;
	public float bglight;
	public float weathereff;
	public float partialTicks;
	public final EnumRenderPass pass;
	
	public StellarRenderInfo(Minecraft mc, Tessellator tessellator, VertexBuffer worldrenderer, float[] skycolor, float weathereff, float partialTicks, EnumRenderPass pass) {
		this.mc = mc;
		this.tessellator = tessellator;
		this.worldrenderer = worldrenderer;
		this.bglight = skycolor[0] + skycolor[1] + skycolor[2];
		this.weathereff = weathereff;
		this.partialTicks = partialTicks;
		this.pass = pass;
	}
}
