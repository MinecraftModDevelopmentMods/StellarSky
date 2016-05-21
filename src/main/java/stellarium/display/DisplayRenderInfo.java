package stellarium.display;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import stellarium.render.EnumRenderPass;

public class DisplayRenderInfo {
	public final Minecraft mc;
	public final Tessellator tessellator;
	public final VertexBuffer worldRenderer;
	public final float partialTicks;
	
	/**
	 * Render pass which display is rendered.
	 * To accept this as false, the depth should be farther than {@link EnumRenderPass#getDeepDepth()}.
	 * */
	public final boolean isPostCelesitals;
	
	public DisplayRenderInfo(Minecraft mc, Tessellator tessellator, VertexBuffer worldRenderer, float partialTicks, boolean isPostCelesitals) {
		this.mc = mc;
		this.tessellator = tessellator;
		this.worldRenderer = worldRenderer;
		this.partialTicks = partialTicks;
		this.isPostCelesitals = isPostCelesitals;
	}
	
}
