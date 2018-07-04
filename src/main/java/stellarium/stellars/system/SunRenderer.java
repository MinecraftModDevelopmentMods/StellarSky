package stellarium.stellars.system;

import org.lwjgl.opengl.GL11;

import stellarium.StellarSkyResources;
import stellarium.render.stellars.access.EnumStellarPass;
import stellarium.render.stellars.layer.LayerRHelper;
import stellarium.render.util.FloatVertexFormats;
import stellarium.stellars.render.ICelestialObjectRenderer;

public enum SunRenderer implements ICelestialObjectRenderer<SunRenderCache> {

	INSTANCE;

	@Override
	public void render(SunRenderCache cache, EnumStellarPass pass, LayerRHelper info) {
		if(pass == EnumStellarPass.DominateScatter) {
			info.renderDominate(cache.appPos, 1.0f, 1.0f, 1.0f);
		} else if(pass == EnumStellarPass.Opaque) {
			info.bindTexture(StellarSkyResources.resourceSunSurface.getLocation());

			info.bindTexShader();
			info.builder.begin(GL11.GL_QUADS, FloatVertexFormats.POSITION_TEX_COLOR_F_NORMAL);

			// TODO AA Solve problem with the expression range - use 100x brightness framebuffer?
			// Minimum is 6.10e-5, 6mag star has magnitude of 1.58e-3 - Impossible to be in expression range with the sun
			float brightness = 4830000.0f; // Should be 4,830,000

			int longc, latc;

			for(longc=0; longc<cache.longn; longc++){
				for(latc=0; latc<cache.latn; latc++){
					int longcd=(longc+1)%cache.longn;
					float longd=(float)longc/(float)cache.longn;
					float latd=1.0f-(float)latc/(float)cache.latn;
					float longdd=(float)(longc+1)/(float)cache.longn;
					float latdd=1.0f-(float)(latc+1)/(float)cache.latn;

					info.builder.pos(cache.sunPos[longc][latc], LayerRHelper.DEEP_DEPTH * 0.8f);
					info.builder.tex(longd, latd);
					info.builder.color(brightness, brightness, brightness,
							1.0f);
					info.builder.normal(cache.sunNormal[longc][latc]);
					info.builder.endVertex();

					info.builder.pos(cache.sunPos[longcd][latc], LayerRHelper.DEEP_DEPTH * 0.8f);
					info.builder.tex(longdd, latd);
					info.builder.color(brightness, brightness, brightness,
							1.0f);
					info.builder.normal(cache.sunNormal[longcd][latc]);
					info.builder.endVertex();

					info.builder.pos(cache.sunPos[longcd][latc+1], LayerRHelper.DEEP_DEPTH * 0.8f);
					info.builder.tex(longdd, latdd);
					info.builder.color(brightness, brightness, brightness,
							1.0f);
					info.builder.normal(cache.sunNormal[longcd][latc+1]);
					info.builder.endVertex();

					info.builder.pos(cache.sunPos[longc][latc+1], LayerRHelper.DEEP_DEPTH * 0.8f);
					info.builder.tex(longd, latdd);
					info.builder.color(brightness, brightness, brightness,
							1.0f);
					info.builder.normal(cache.sunNormal[longc][latc+1]);
					info.builder.endVertex();
				}
			}

			info.builder.finishDrawing();
			info.renderer.draw(info.builder);
			info.unbindTexShader();
		}
	}
}