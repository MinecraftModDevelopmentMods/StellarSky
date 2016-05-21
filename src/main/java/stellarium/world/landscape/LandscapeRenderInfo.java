package stellarium.world.landscape;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;

public class LandscapeRenderInfo {
	public final Minecraft mc;
	public final Tessellator tessellator;
	public final VertexBuffer worldRenderer;
	public final float partialTicks;
	
	public LandscapeRenderInfo(Minecraft mc, Tessellator tessellator, VertexBuffer worldRenderer, float partialTicks) {
		this.mc = mc;
		this.tessellator = tessellator;
		this.worldRenderer = worldRenderer;
		this.partialTicks = partialTicks;
	}
}
