package stellarium.stellars.system;

import org.lwjgl.opengl.GL11;

import stellarium.StellarSkyResources;
import stellarium.render.stellars.CRenderHelper;
import stellarium.render.stellars.access.EnumStellarPass;
import stellarium.render.stellars.layer.LayerRI;
import stellarium.render.util.FloatVertexFormats;
import stellarium.stellars.render.ICelestialObjectRenderer;

public enum MoonRenderer implements ICelestialObjectRenderer<MoonRenderCache> {
	INSTANCE;

	@Override
	public void render(MoonRenderCache cache, EnumStellarPass pass, LayerRI info) {
		CRenderHelper helper = info.helper;
		if(pass == EnumStellarPass.DominateScatter && cache.shouldRenderDominate) {
			helper.setup();
			helper.renderDominate(cache.appPos, cache.domination, cache.domination, cache.domination);
		} else if(pass == EnumStellarPass.OpaqueScatter && cache.shouldRender) {
			// TODO Implement opaque scatter case for the moon
		} else if(pass == EnumStellarPass.Opaque && cache.shouldRender) {
			helper.bindTexture(StellarSkyResources.resourceMoonSurface.getLocation());
			helper.setup();
			info.builder.begin(GL11.GL_QUADS, FloatVertexFormats.POSITION_TEX_COLOR_F_NORMAL);

			int longc, latc;

			for(longc=0; longc<cache.longn; longc++){
				for(latc=0; latc<cache.latn; latc++){
					int longcd=(longc+1)%cache.longn;
					float longd=(float)longc/(float)cache.longn + 0.5f;
					float latd=1.0f-(float)latc/(float)cache.latn;
					float longdd=(float)(longc+1)/(float)cache.longn + 0.5f;
					float latdd=1.0f-(float)(latc+1)/(float)cache.latn;

					info.builder.pos(cache.moonPos[longc][latc], info.deepDepth * 0.5f);
					info.builder.tex(longd, latd);
					info.builder.color(cache.moonilum[longc][latc] * helper.multRed(),
							cache.moonilum[longc][latc] * helper.multGreen(),
							cache.moonilum[longc][latc] * helper.multBlue(),
							1.0f);
					info.builder.normal(cache.moonnormal[longc][latc]);
					info.builder.endVertex();

					info.builder.pos(cache.moonPos[longcd][latc], info.deepDepth * 0.5f);
					info.builder.tex(longdd, latd);
					info.builder.color(cache.moonilum[longcd][latc] * helper.multRed(),
							cache.moonilum[longcd][latc] * helper.multGreen(),
							cache.moonilum[longcd][latc] * helper.multBlue(),
							1.0f);
					info.builder.normal(cache.moonnormal[longcd][latc]);
					info.builder.endVertex();
					
					info.builder.pos(cache.moonPos[longcd][latc+1], info.deepDepth * 0.5f);
					info.builder.tex(longdd, latdd);
					info.builder.color(cache.moonilum[longcd][latc+1] * helper.multRed(),
							cache.moonilum[longcd][latc+1] * helper.multGreen(),
							cache.moonilum[longcd][latc+1] * helper.multBlue(), 
							1.0f);
					info.builder.normal(cache.moonnormal[longcd][latc+1]);
					info.builder.endVertex();
					
					info.builder.pos(cache.moonPos[longc][latc+1], info.deepDepth * 0.5f);
					info.builder.tex(longd, latdd);
					info.builder.color(cache.moonilum[longc][latc+1] * helper.multRed(),
							cache.moonilum[longc][latc+1] * helper.multGreen(),
							cache.moonilum[longc][latc+1] * helper.multBlue(),
							1.0f);
					info.builder.normal(cache.moonnormal[longc][latc+1]);
					info.builder.endVertex();
				}
			}
			
			info.tessellator.draw();
		}
	}

}
