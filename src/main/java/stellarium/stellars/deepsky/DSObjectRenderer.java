package stellarium.stellars.deepsky;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import stellarium.render.stellars.access.EnumStellarPass;
import stellarium.render.stellars.layer.LayerRHelper;
import stellarium.stellars.render.ICelestialObjectRenderer;

public enum DSObjectRenderer implements ICelestialObjectRenderer<DeepSkyObjectCache> {

	INSTANCE;

	@Override
	public void render(DeepSkyObjectCache cache, EnumStellarPass pass, LayerRHelper info) {
		if(!cache.shouldRender)
			return;

		// MAYBE Searching for this should be hard objective
		info.bindTexture(cache.location);

		GlStateManager.color(cache.surfBr * 1.0f, cache.surfBr * 1.0f, cache.surfBr * 1.0f);

		info.builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

		info.builder.pos(cache.coords[0]).tex(1, 0).endVertex();
		info.builder.pos(cache.coords[1]).tex(0, 0).endVertex();
		info.builder.pos(cache.coords[2]).tex(0, 1).endVertex();
		info.builder.pos(cache.coords[3]).tex(1, 1).endVertex();

		info.builder.finishDrawing();
		info.renderer.draw(info.builder);
	}

}
