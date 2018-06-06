package stellarium.stellars.system;

import org.lwjgl.opengl.GL11;

import stellarium.StellarSkyResources;
import stellarium.render.stellars.CRenderHelper;
import stellarium.render.stellars.access.EnumStellarPass;
import stellarium.render.stellars.layer.LayerRI;
import stellarium.render.util.FloatVertexFormats;
import stellarium.stellars.render.ICelestialObjectRenderer;

public enum SunRenderer implements ICelestialObjectRenderer<SunRenderCache> {
	
	INSTANCE;

	@Override
	public void render(SunRenderCache cache, EnumStellarPass pass, LayerRI info) {
		CRenderHelper helper = info.helper;

		if(pass == EnumStellarPass.DominateScatter) {
			helper.setup();
			helper.renderDominate(cache.appPos, 1.0f, 1.0f, 1.0f);
		} else if(pass == EnumStellarPass.Opaque) {
			helper.bindTexture(StellarSkyResources.resourceSunSurface.getLocation());
			helper.setup();
			info.builder.begin(GL11.GL_QUADS, FloatVertexFormats.POSITION_TEX_COLOR_F_NORMAL);

			float brightness = 10000.0f;
			
			int longc, latc;

			for(longc=0; longc<cache.longn; longc++){
				for(latc=0; latc<cache.latn; latc++){
					int longcd=(longc+1)%cache.longn;
					float longd=(float)longc/(float)cache.longn;
					float latd=1.0f-(float)latc/(float)cache.latn;
					float longdd=(float)(longc+1)/(float)cache.longn;
					float latdd=1.0f-(float)(latc+1)/(float)cache.latn;

					info.builder.pos(cache.sunPos[longc][latc], info.deepDepth * 0.8f);
					info.builder.tex(longd, latd);
					info.builder.color(brightness * helper.multRed(),
							brightness * helper.multGreen(),
							brightness * helper.multBlue(),
							1.0f);
					info.builder.normal(cache.sunNormal[longc][latc]);
					info.builder.endVertex();
					
					info.builder.pos(cache.sunPos[longcd][latc], info.deepDepth * 0.8f);
					info.builder.tex(longdd, latd);
					info.builder.color(brightness * helper.multRed(),
							brightness * helper.multGreen(),
							brightness * helper.multBlue(),
							1.0f);
					info.builder.normal(cache.sunNormal[longcd][latc]);
					info.builder.endVertex();
					
					info.builder.pos(cache.sunPos[longcd][latc+1], info.deepDepth * 0.8f);
					info.builder.tex(longdd, latdd);
					info.builder.color(brightness * helper.multRed(),
							brightness * helper.multGreen(),
							brightness * helper.multBlue(),
							1.0f);
					info.builder.normal(cache.sunNormal[longcd][latc+1]);
					info.builder.endVertex();
					
					info.builder.pos(cache.sunPos[longc][latc+1], info.deepDepth * 0.8f);
					info.builder.tex(longd, latdd);
					info.builder.color(brightness * helper.multRed(),
							brightness * helper.multGreen(),
							brightness * helper.multBlue(),
							1.0f);
					info.builder.normal(cache.sunNormal[longc][latc+1]);
					info.builder.endVertex();
				}
			}

			info.tessellator.draw();
		}
	}
}