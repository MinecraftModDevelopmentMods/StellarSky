package stellarium.render.sky;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.Tessellator;
import stellarium.view.ViewerInfo;

public class SkyRenderInformation {
	public final Minecraft minecraft;
	public final WorldClient world;
	public final Tessellator tessellator;
	public final float partialTicks;
	
	public final boolean isFrameBufferEnabled;
	public final double deepDepth;
	
	public final ViewerInfo info;
	public final double screenSize;
}
