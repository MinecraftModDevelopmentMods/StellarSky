package stellarium.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;

public class StellarRenderInfo {

	public Minecraft mc;
	public Tessellator tessellator;
	public WorldRenderer worldrenderer;
	public float bglight;
	public float weathereff;
	public float partialTicks;
	
	public StellarRenderInfo(Minecraft mc, Tessellator tessellator, WorldRenderer worldrenderer, float bglight, float weathereff, float partialTicks) {
		this.mc = mc;
		this.tessellator = tessellator;
		this.worldrenderer = worldrenderer;
		this.bglight = bglight;
		this.weathereff = weathereff;
		this.partialTicks = partialTicks;
	}

}
