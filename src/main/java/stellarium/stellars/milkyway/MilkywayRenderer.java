package stellarium.stellars.milkyway;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import stellarium.StellarSkyResources;
import stellarium.render.stellars.access.EnumStellarPass;
import stellarium.render.stellars.access.IStellarTessellator;
import stellarium.render.stellars.layer.LayerRenderInformation;
import stellarium.stellars.render.ICelestialObjectRenderer;

public enum MilkywayRenderer implements ICelestialObjectRenderer<MilkywayRenderCache> {

	INSTANCE;

	@Override
	public void render(MilkywayRenderCache cache, EnumStellarPass pass, LayerRenderInformation info) {
		IStellarTessellator tessellator = info.tessellator;

		tessellator.begin(true);
		tessellator.bindTexture(StellarSkyResources.resourceMilkyway.getLocation());
		tessellator.color(cache.milkywayAbsBr * 10.0f, cache.milkywayAbsBr * 10.0f, cache.milkywayAbsBr * 10.0f);

		for(int longc=0; longc<cache.longn; longc++){
			for(int latc=0; latc<cache.latn; latc++){
				int longcd=(longc+1)%cache.longn;
				float longd=1.0f-(float)longc/(float)cache.longn;
				float latd=1.0f-(float)latc/(float)cache.latn;
				float longdd=1.0f-(float)(longc+1)/(float)cache.longn;
				float latdd=1.0f-(float)(latc+1)/(float)cache.latn;

				tessellator.pos(cache.milkywaypos[longc][latc], info.deepDepth * 0.6f);
				tessellator.normal(cache.milkywaypos[longc][latc]);
				tessellator.texture(longd, latd);
				tessellator.writeVertex();

				tessellator.pos(cache.milkywaypos[longc][latc+1], info.deepDepth * 0.6f);
				tessellator.normal(cache.milkywaypos[longc][latc+1]);
				tessellator.texture(longd, latdd);
				tessellator.writeVertex();

				tessellator.pos(cache.milkywaypos[longcd][latc+1], info.deepDepth * 0.6f);
				tessellator.normal(cache.milkywaypos[longcd][latc+1]);
				tessellator.texture(longdd, latdd);
				tessellator.writeVertex();

				tessellator.pos(cache.milkywaypos[longcd][latc], info.deepDepth * 0.6f);
				tessellator.normal(cache.milkywaypos[longcd][latc]);
				tessellator.texture(longdd, latd);
				tessellator.writeVertex();
			}
		}

		GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		tessellator.end();
		GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
	}

}
