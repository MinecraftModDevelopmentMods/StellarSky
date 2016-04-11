package stellarium.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;

public class StellarRenderInfo {
	public final Minecraft mc;
	public final Tessellator tessellator;
	public final float bglight;
	public final float weathereff;
	public final float partialTicks;
	
	public StellarRenderInfo(Minecraft mc, Tessellator tessellator, float bglight, float weathereff, float partialTicks) {
		this.mc = mc;
		this.tessellator = tessellator;
		this.bglight = bglight;
		this.weathereff = weathereff;
		this.partialTicks = partialTicks;
	}
}
