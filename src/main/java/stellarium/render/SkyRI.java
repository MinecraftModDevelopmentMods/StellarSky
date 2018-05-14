package stellarium.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import stellarium.view.ViewerInfo;

public class SkyRI {
	public final Minecraft minecraft;
	public final WorldClient world;
	public final Tessellator tessellator;
	public final BufferBuilder worldRenderer;
	public final float partialTicks;
	
	public final float deepDepth;
	
	public final ViewerInfo info;
	public final double screenSize;
	
	public SkyRI(Minecraft mc, WorldClient theWorld, float partialTicks, ViewerInfo viewer) {
		this.minecraft = mc;
		this.world = theWorld;
		this.tessellator = Tessellator.getInstance();
		this.worldRenderer = tessellator.getBuffer();
		this.partialTicks = partialTicks;
		
		this.deepDepth = 100.0f;

		this.info = viewer;
		this.screenSize = mc.displayWidth;
	}
}