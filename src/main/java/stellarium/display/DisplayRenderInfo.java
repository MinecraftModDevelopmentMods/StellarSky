package stellarium.display;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;


public class DisplayRenderInfo {
	public final Minecraft mc;
	public final Tessellator tessellator;
	public final BufferBuilder builder;
	public final float partialTicks;

	/**
	 * Render pass which display is rendered.
	 * To accept this as false, the depth should be farther than {@link EnumRenderPass#getDeepDepth()}.
	 * */
	public final boolean isPostCelesitals;

	public DisplayRenderInfo(Minecraft mc, Tessellator tessellator, BufferBuilder worldRenderer, float partialTicks, boolean isPostCelesitals) {
		this.mc = mc;
		this.tessellator = tessellator;
		this.builder = worldRenderer;
		this.partialTicks = partialTicks;
		this.isPostCelesitals = isPostCelesitals;
	}
}
