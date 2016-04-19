package stellarium.stellars.milkyway;

import org.lwjgl.opengl.GL11;

import stellarium.StellarSkyResources;
import stellarium.render.ICelestialObjectRenderer;
import stellarium.render.StellarRenderInfo;
import stellarium.stellars.Optics;

public class MilkywayRenderer implements ICelestialObjectRenderer<MilkywayCache> {

	@Override
	public void render(StellarRenderInfo info, MilkywayCache cache) {
		GL11.glColor4f(1.0f, 1.0f, 1.0f, info.weathereff);

		info.mc.renderEngine.bindTexture(StellarSkyResources.resourceMilkyway.getLocation());
		info.tessellator.startDrawingQuads();
		
		float Mag = 3.5f;
		float alpha=Optics.getAlphaForGalaxy(Mag, info.bglight) - (((1-info.weathereff)/1)*20f);
		info.tessellator.setColorRGBA_F(1.0f, 1.0f, 1.0f, cache.brightness * alpha);

		for(int longc=0; longc<cache.longn; longc++){
			for(int latc=0; latc<cache.latn; latc++){
				int longcd=(longc+1)%cache.longn;
				double longd=1.0-(double)longc/(double)cache.longn;
				double latd=1.0-(double)latc/(double)cache.latn;
				double longdd=1.0-(double)(longc+1)/(double)cache.longn;
				double latdd=1.0-(double)(latc+1)/(double)cache.latn;

				info.tessellator.addVertexWithUV(cache.moonvec[longc][latc].x, cache.moonvec[longc][latc].y, cache.moonvec[longc][latc].z, longd, latd);
				info.tessellator.addVertexWithUV(cache.moonvec[longc][latc+1].x, cache.moonvec[longc][latc+1].y, cache.moonvec[longc][latc+1].z, longd, latdd);
				info.tessellator.addVertexWithUV(cache.moonvec[longcd][latc+1].x, cache.moonvec[longcd][latc+1].y, cache.moonvec[longcd][latc+1].z, longdd, latdd);
				info.tessellator.addVertexWithUV(cache.moonvec[longcd][latc].x, cache.moonvec[longcd][latc].y, cache.moonvec[longcd][latc].z, longdd, latd);
			}
		}
		info.tessellator.draw();
	}

}
