package stellarium.render.stellars.layer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import stellarium.render.stellars.StellarTessellator;
import stellarium.render.stellars.access.EnumStellarPass;
import stellarium.render.stellars.access.IStellarTessellator;
import stellarium.render.stellars.phased.StellarRenderInformation;

public class LayerRenderInformation {
	public final Minecraft minecraft;
	public final WorldClient world;
	public final float partialTicks;
	public final float deepDepth;

	public final IStellarTessellator tessellator;

	public LayerRenderInformation(StellarRenderInformation info, StellarTessellator tessellator) {
		this.minecraft = info.minecraft;
		this.world = info.world;
		this.partialTicks = info.partialTicks;
		this.deepDepth = info.deepDepth;
		
		this.tessellator = tessellator;
		tessellator.initialize(info);
	}

	public void initialize(EnumStellarPass pass) {
		tessellator.initializePass(pass);
	}
}
