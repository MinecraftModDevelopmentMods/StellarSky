package stellarium.stellars.milkyway;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import stellarium.StellarSkyResources;
import stellarium.render.stellars.CRenderHelper;
import stellarium.render.stellars.access.EnumStellarPass;
import stellarium.render.stellars.layer.LayerRI;
import stellarium.stellars.render.ICelestialObjectRenderer;

public enum MilkywayRenderer implements ICelestialObjectRenderer<MilkywayRenderCache> {

	INSTANCE;

	@Override
	public void render(MilkywayRenderCache cache, EnumStellarPass pass, LayerRI info) {
		CRenderHelper tessellator = info.helper;

		info.bindTexture(StellarSkyResources.resourceMilkyway.getLocation());
		// TODO Proper milky way texture filtering
		GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

		GlStateManager.color(cache.surfBr, cache.surfBr, cache.surfBr);

		info.builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);

		for(int longc=0; longc<cache.longn; longc++){
			for(int latc=0; latc<cache.latn; latc++){
				int longcd=(longc+1)%cache.longn;
				float longd=1.0f-(float)longc/(float)cache.longn;
				float latd=1.0f-(float)latc/(float)cache.latn;
				float longdd=1.0f-(float)(longc+1)/(float)cache.longn;
				float latdd=1.0f-(float)(latc+1)/(float)cache.latn;

				info.builder.pos(cache.milkywayNormal[longc][latc], CRenderHelper.DEEP_DEPTH);
				info.builder.tex(longd, latd);
				info.builder.normal(cache.milkywayNormal[longc][latc]);
				info.builder.endVertex();

				info.builder.pos(cache.milkywayNormal[longc][latc+1], CRenderHelper.DEEP_DEPTH);
				info.builder.tex(longd, latdd);
				info.builder.normal(cache.milkywayNormal[longc][latc+1]);
				info.builder.endVertex();

				info.builder.pos(cache.milkywayNormal[longcd][latc+1], CRenderHelper.DEEP_DEPTH);
				info.builder.tex(longdd, latdd);
				info.builder.normal(cache.milkywayNormal[longcd][latc+1]);
				info.builder.endVertex();

				info.builder.pos(cache.milkywayNormal[longcd][latc], CRenderHelper.DEEP_DEPTH);
				info.builder.tex(longdd, latd);
				info.builder.normal(cache.milkywayNormal[longcd][latc]);
				info.builder.endVertex();
			}
		}

		info.tessellator.draw();
	}

}
