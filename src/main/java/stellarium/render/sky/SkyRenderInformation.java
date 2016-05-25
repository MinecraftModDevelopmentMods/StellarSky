package stellarium.render.sky;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;

public class SkyRenderInformation {
	public final Minecraft minecraft;
	public final WorldClient world;
	public final float partialTicks;
	public final boolean isFrameBufferEnabled;
	public final double deepDepth;
}
