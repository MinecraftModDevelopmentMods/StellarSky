package stellarium.display;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import stellarium.render.EnumRenderPass;

public class DisplayRenderInfo {
	public final Minecraft mc;
	public final Tessellator tessellator;
	public final float partialTicks;
	public final boolean isPostCelesitals;
	
	public DisplayRenderInfo(Minecraft mc, Tessellator tessellator, float partialTicks, boolean isPostCelesitals) {
		this.mc = mc;
		this.tessellator = tessellator;
		this.partialTicks = partialTicks;
		this.isPostCelesitals = isPostCelesitals;
	}
	
}
