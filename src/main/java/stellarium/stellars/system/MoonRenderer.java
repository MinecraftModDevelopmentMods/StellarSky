package stellarium.stellars.system;

import org.lwjgl.opengl.GL11;

import stellarium.StellarSkyResources;
import stellarium.render.stellars.access.EnumStellarPass;
import stellarium.render.stellars.layer.LayerRHelper;
import stellarium.render.util.FloatVertexFormats;
import stellarium.stellars.render.ICelestialObjectRenderer;

public enum MoonRenderer implements ICelestialObjectRenderer<MoonRenderCache> {
	INSTANCE;

	@Override
	public void render(MoonRenderCache cache, EnumStellarPass pass, LayerRHelper info) {
		if(pass == EnumStellarPass.DominateScatter && cache.shouldRenderDominate) {
			// TODO Make things fast enough so that I can use dominate scattering on moon as well
			// Please, it's quite pretty =/
			//helper.setup();
			//helper.renderDominate(cache.appPos, cache.domination, cache.domination, cache.domination);
		} else if(pass == EnumStellarPass.Opaque && cache.shouldRender) {
			info.bindTexture(StellarSkyResources.resourceMoonSurface.getLocation());
			info.builder.begin(GL11.GL_QUADS, FloatVertexFormats.POSITION_TEX_COLOR_F_NORMAL);

			int longc, latc;

			for(longc=0; longc<cache.longn; longc++){
				for(latc=0; latc<cache.latn; latc++){
					int longcd=(longc+1)%cache.longn;
					float longd=(float)longc/(float)cache.longn + 0.5f;
					float latd=1.0f-(float)latc/(float)cache.latn;
					float longdd=(float)(longc+1)/(float)cache.longn + 0.5f;
					float latdd=1.0f-(float)(latc+1)/(float)cache.latn;

					info.builder.pos(cache.pos[longc][latc], LayerRHelper.DEEP_DEPTH * 0.5f);
					info.builder.tex(longd, latd);
					info.builder.color(cache.surfBr[longc][latc],
							cache.surfBr[longc][latc],
							cache.surfBr[longc][latc],
							1.0f);
					info.builder.normal(cache.normal[longc][latc]);
					info.builder.endVertex();

					info.builder.pos(cache.pos[longcd][latc], LayerRHelper.DEEP_DEPTH * 0.5f);
					info.builder.tex(longdd, latd);
					info.builder.color(cache.surfBr[longcd][latc],
							cache.surfBr[longcd][latc],
							cache.surfBr[longcd][latc],
							1.0f);
					info.builder.normal(cache.normal[longcd][latc]);
					info.builder.endVertex();
					
					info.builder.pos(cache.pos[longcd][latc+1], LayerRHelper.DEEP_DEPTH * 0.5f);
					info.builder.tex(longdd, latdd);
					info.builder.color(cache.surfBr[longcd][latc+1],
							cache.surfBr[longcd][latc+1],
							cache.surfBr[longcd][latc+1], 
							1.0f);
					info.builder.normal(cache.normal[longcd][latc+1]);
					info.builder.endVertex();

					info.builder.pos(cache.pos[longc][latc+1], LayerRHelper.DEEP_DEPTH * 0.5f);
					info.builder.tex(longd, latdd);
					info.builder.color(cache.surfBr[longc][latc+1],
							cache.surfBr[longc][latc+1],
							cache.surfBr[longc][latc+1],
							1.0f);
					info.builder.normal(cache.normal[longc][latc+1]);
					info.builder.endVertex();
				}
			}

			info.builder.finishDrawing();
			info.renderer.draw(info.builder);
		}
	}

}
