package stellarium.render.stellars.layer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import stellarium.render.stellars.CRenderHelper;
import stellarium.render.stellars.StellarRI;
import stellarium.render.stellars.access.EnumStellarPass;
import stellarium.render.util.BufferBuilderEx;
import stellarium.render.util.TessellatorEx;

public class LayerRI {
	public final Minecraft minecraft;
	public final WorldClient world;
	public final float partialTicks;
	public final float deepDepth;
	public final StellarRI stellarInfo;

	public final CRenderHelper helper;
	public final TessellatorEx tessellator;
	public final BufferBuilderEx builder;

	public LayerRI(StellarRI info, CRenderHelper helper) {
		this.minecraft = info.minecraft;
		this.world = info.world;
		this.partialTicks = info.partialTicks;
		this.deepDepth = info.deepDepth;
		this.stellarInfo = info;

		this.helper = helper;
		this.tessellator = TessellatorEx.getInstance();
		this.builder = tessellator.getBuffer();

		helper.initialize(info);
	}

	public void initialize(EnumStellarPass pass) {
		helper.initializePass(pass);
	}
}
