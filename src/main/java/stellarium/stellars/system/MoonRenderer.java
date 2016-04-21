package stellarium.stellars.system;

import javax.vecmath.Vector3d;

import org.lwjgl.opengl.GL11;

import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Spmath;
import stellarium.StellarSkyResources;
import stellarium.render.ICelestialObjectRenderer;
import stellarium.render.StellarRenderInfo;
import stellarium.stellars.Optics;

public class MoonRenderer implements ICelestialObjectRenderer<MoonRenderCache> {
	
	@Override
	public void render(StellarRenderInfo info, MoonRenderCache cache) {
		
		info.mc.renderEngine.bindTexture(StellarSkyResources.resourceMoonHalo.getLocation());

		if(cache.shouldRenderGlow){
			Vector3d pos = cache.appCoord.getVec();
			Vector3d dif = new SpCoord(cache.appCoord.x+90, 0.0).getVec();
			Vector3d dif2 = new SpCoord(cache.appCoord.x, cache.appCoord.y+90).getVec();
			
			pos.scale(98.0);
			dif.scale(cache.size);
			dif2.scale(-cache.size);
			
			float alpha=(float) (Optics.getAlphaFromMagnitude(14.5+cache.appMag-2.5*Math.log10(cache.difactor),info.bglight));
			

			info.tessellator.startDrawingQuads();
			info.tessellator.setColorRGBA_F(1.0f, 1.0f, 1.0f, alpha);
			info.tessellator.addVertexWithUV(pos.x+dif.x, pos.y+dif.y, pos.z+dif.z,0.0,0.0);
			info.tessellator.addVertexWithUV(pos.x+dif2.x, pos.y+dif2.y, pos.z+dif2.z,0.0,1.0);
			info.tessellator.addVertexWithUV(pos.x-dif.x, pos.y-dif.y, pos.z-dif.z,1.0,1.0);
			info.tessellator.addVertexWithUV(pos.x-dif2.x, pos.y-dif2.y, pos.z-dif2.z,1.0,0.0);
			info.tessellator.draw();
		}


		info.mc.renderEngine.bindTexture(StellarSkyResources.resourceMoonSurface.getLocation());

		info.tessellator.startDrawingQuads();
		
		int longc, latc;

		for(longc=0; longc<cache.longn; longc++){
			for(latc=0; latc<cache.latn; latc++){

				int longcd=(longc+1)%cache.longn;
				double longd=(double)longc/(double)cache.longn;
				double latd=1.0-(double)latc/(double)cache.latn;
				double longdd=(double)(longc+1)/(double)cache.longn;
				double latdd=1.0-(double)(latc+1)/(double)cache.latn;

				float lightlevel = (0.875f*(info.bglight/2.1333334f));
				info.tessellator.setColorRGBA_F(1.0f - lightlevel, 1.0f - lightlevel, 1.0f - lightlevel, ((info.weathereff*cache.moonilum[longc][latc]-0.015f*info.bglight)*2.0f));
				
				info.tessellator.setNormal((float)cache.moonnormal[longc][latc].x, (float)cache.moonnormal[longc][latc].y, (float)cache.moonnormal[longc][latc].z);
				info.tessellator.addVertexWithUV(cache.moonPos[longc][latc].x, cache.moonPos[longc][latc].y, cache.moonPos[longc][latc].z, Spmath.fmod(longd+0.5, 1.0), latd);
				
				info.tessellator.setNormal((float)cache.moonnormal[longcd][latc].x, (float)cache.moonnormal[longcd][latc].y, (float)cache.moonnormal[longcd][latc].z);
				info.tessellator.addVertexWithUV(cache.moonPos[longcd][latc].x, cache.moonPos[longcd][latc].y, cache.moonPos[longcd][latc].z, Spmath.fmod(longdd+0.5, 1.0), latd);
				
				info.tessellator.setNormal((float)cache.moonnormal[longcd][latc+1].x, (float)cache.moonnormal[longcd][latc+1].y, (float)cache.moonnormal[longcd][latc+1].z);
				info.tessellator.addVertexWithUV(cache.moonPos[longcd][latc+1].x, cache.moonPos[longcd][latc+1].y, cache.moonPos[longcd][latc+1].z, Spmath.fmod(longdd+0.5, 1.0), latdd);
				
				info.tessellator.setNormal((float)cache.moonnormal[longc][latc+1].x, (float)cache.moonnormal[longc][latc+1].y, (float)cache.moonnormal[longc][latc+1].z);
				info.tessellator.addVertexWithUV(cache.moonPos[longc][latc+1].x, cache.moonPos[longc][latc+1].y, cache.moonPos[longc][latc+1].z, Spmath.fmod(longd+0.5, 1.0), latdd);
			}
		}

		info.tessellator.draw();
	}

}
