package stellarium.world.landscape;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import stellarium.render.celesital.EnumRenderPass;

public class LandscapeRenderInfo {
	public final Minecraft mc;
	public final Tessellator tessellator;
	public final float partialTicks;
	
	public LandscapeRenderInfo(Minecraft mc, Tessellator tessellator, float partialTicks) {
		this.mc = mc;
		this.tessellator = tessellator;
		this.partialTicks = partialTicks;
	}
}
