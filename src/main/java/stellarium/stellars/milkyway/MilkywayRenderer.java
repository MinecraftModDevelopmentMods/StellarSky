package stellarium.stellars.milkyway;

import org.lwjgl.opengl.GL11;

import stellarapi.api.lib.math.Spmath;
import stellarium.StellarSkyResources;
import stellarium.render.ICelestialObjectRenderer;
import stellarium.render.StellarRenderInfo;
import stellarium.stellars.Optics;

public class MilkywayRenderer implements ICelestialObjectRenderer<MilkywayRenderCache> {

	@Override
	public void render(StellarRenderInfo info, MilkywayRenderCache cache) {
		GL11.glColor4f(1.0f, 1.0f, 1.0f, info.weathereff);

		info.mc.renderEngine.bindTexture(StellarSkyResources.resourceMilkyway.getLocation());
		
		float Mag = 3.5f;
		float alpha=Optics.getAlphaForGalaxy(Mag, info.bglight) - (((1-info.weathereff)/1)*20f);
		
		info.tessellator.startDrawingQuads();
		info.tessellator.setColorRGBA_F((float)cache.color[0], (float)cache.color[1], (float)cache.color[2], cache.brightness * alpha);

		for(int longc=0; longc<cache.longn; longc++){
			for(int latc=0; latc<cache.latn; latc++){
				int longcd=(longc+1)%cache.longn;
				double longd=1.0-(double)longc/(double)cache.longn;
				double latd=1.0-(double)latc/(double)cache.latn;
				double longdd=1.0-(double)(longc+1)/(double)cache.longn;
				double latdd=1.0-(double)(latc+1)/(double)cache.latn;

				info.tessellator.addVertexWithUV(cache.milkywayvec[longc][latc].x, cache.milkywayvec[longc][latc].y, cache.milkywayvec[longc][latc].z, longd, latd);
				info.tessellator.addVertexWithUV(cache.milkywayvec[longc][latc+1].x, cache.milkywayvec[longc][latc+1].y, cache.milkywayvec[longc][latc+1].z, longd, latdd);
				info.tessellator.addVertexWithUV(cache.milkywayvec[longcd][latc+1].x, cache.milkywayvec[longcd][latc+1].y, cache.milkywayvec[longcd][latc+1].z, longdd, latdd);
				info.tessellator.addVertexWithUV(cache.milkywayvec[longcd][latc].x, cache.milkywayvec[longcd][latc].y, cache.milkywayvec[longcd][latc].z, longdd, latd);
			}
		}
		info.tessellator.draw();
	}

}
