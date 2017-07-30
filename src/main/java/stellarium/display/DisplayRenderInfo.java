package stellarium.display;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import stellarium.render.EnumRenderPass;


public class DisplayRenderInfo {
	public final Minecraft mc;
	public final Tessellator tessellator;
	public final BufferBuilder worldRenderer;
	public final float partialTicks;
	public final float deepDepth;

	/**
	 * Render pass which display is rendered.
	 * To accept this as false, the depth should be farther than {@link EnumRenderPass#getDeepDepth()}.
	 * */
	public final boolean isPostCelesitals;

	public DisplayRenderInfo(Minecraft mc, Tessellator tessellator, BufferBuilder worldRenderer, float partialTicks, boolean isPostCelesitals, float deepDepth) {
		this.mc = mc;
		this.tessellator = tessellator;
		this.worldRenderer = worldRenderer;
		this.partialTicks = partialTicks;
		this.isPostCelesitals = isPostCelesitals;
		this.deepDepth = deepDepth;
	}
}
