package stellarium.stellars.deepsky;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import stellarium.render.stellars.access.EnumStellarPass;
import stellarium.render.stellars.layer.LayerRI;
import stellarium.stellars.render.ICelestialObjectRenderer;

public enum DSObjectRenderer implements ICelestialObjectRenderer<DeepSkyObjectCache> {

	INSTANCE;

	@Override
	public void render(DeepSkyObjectCache cache, EnumStellarPass pass, LayerRI info) {
		if(!cache.shouldRender)
			return;

		info.bindTexture(cache.location);

		GlStateManager.color(cache.surfBr, cache.surfBr, cache.surfBr);

		info.builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

		info.builder.pos(cache.coords[0]).tex(1, 0).endVertex();
		info.builder.pos(cache.coords[1]).tex(0, 0).endVertex();
		info.builder.pos(cache.coords[2]).tex(0, 1).endVertex();
		info.builder.pos(cache.coords[3]).tex(1, 1).endVertex();

		info.tessellator.draw();
	}

}
