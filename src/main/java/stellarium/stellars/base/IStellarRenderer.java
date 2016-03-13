package stellarium.stellars.base;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.Tessellator;
import stellarium.stellars.view.IStellarViewpoint;

public interface IStellarRenderer {
	
	public void renderObject(Minecraft mc, WorldClient world, Tessellator tessellator,
			IStellarViewpoint viewpoint, IPerDimensionCache cache);

}
