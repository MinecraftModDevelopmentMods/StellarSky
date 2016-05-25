package stellarium.stellars.milkyway;

import org.lwjgl.opengl.GL11;

import stellarium.StellarSkyResources;
import stellarium.stellars.Optics;
import stellarium.stellars.render.EnumRenderPass;
import stellarium.stellars.render.ICelestialObjectRenderer;
import stellarium.stellars.render.StellarRenderInfo;

public class MilkywayRenderer implements ICelestialObjectRenderer<MilkywayRenderCache> {

	@Override
	public void render(StellarRenderInfo info, MilkywayRenderCache cache) {
		if(info.pass != EnumRenderPass.DeepScattering)
			return;
		
		GL11.glColor4f(1.0f, 1.0f, 1.0f, info.weathereff);

		info.mc.renderEngine.bindTexture(StellarSkyResources.resourceMilkyway.getLocation());
		
		float Mag = 3.5f;
		float alpha=Optics.getAlphaForGalaxy(Mag, info.bglight) - (((1-info.weathereff)/1)*20f);
		
		info.tessellator.startDrawingQuads();
		info.tessellator.setColorRGBA_F((float)cache.color[0], (float)cache.color[1], (float)cache.color[2], (float)cache.color[3] * alpha);

		for(int longc=0; longc<cache.longn; longc++){
			for(int latc=0; latc<cache.latn; latc++){
				int longcd=(longc+1)%cache.longn;
				double longd=1.0-(double)longc/(double)cache.longn;
				double latd=1.0-(double)latc/(double)cache.latn;
				double longdd=1.0-(double)(longc+1)/(double)cache.longn;
				double latdd=1.0-(double)(latc+1)/(double)cache.latn;

				info.tessellator.addVertexWithUV(cache.milkywayvec[longc][latc].getX(), cache.milkywayvec[longc][latc].getY(), cache.milkywayvec[longc][latc].getZ(), longd, latd);
				info.tessellator.addVertexWithUV(cache.milkywayvec[longc][latc+1].getX(), cache.milkywayvec[longc][latc+1].getY(), cache.milkywayvec[longc][latc+1].getZ(), longd, latdd);
				info.tessellator.addVertexWithUV(cache.milkywayvec[longcd][latc+1].getX(), cache.milkywayvec[longcd][latc+1].getY(), cache.milkywayvec[longcd][latc+1].getZ(), longdd, latdd);
				info.tessellator.addVertexWithUV(cache.milkywayvec[longcd][latc].getX(), cache.milkywayvec[longcd][latc].getY(), cache.milkywayvec[longcd][latc].getZ(), longdd, latd);
			}
		}
		info.tessellator.draw();
	}

}
