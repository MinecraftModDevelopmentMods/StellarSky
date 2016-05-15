package stellarium.stellars.system;

import org.lwjgl.opengl.GL11;

import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Vector3;
import stellarium.StellarSkyResources;
import stellarium.render.EnumRenderPass;
import stellarium.render.ICelestialObjectRenderer;
import stellarium.render.StellarRenderInfo;
import stellarium.stellars.Optics;

public class MoonRenderer implements ICelestialObjectRenderer<MoonRenderCache> {
	
	@Override
	public void render(StellarRenderInfo info, MoonRenderCache cache) {
		if(info.pass == EnumRenderPass.ShallowScattering) {
			info.mc.renderEngine.bindTexture(StellarSkyResources.resourceMoonHalo.getLocation());

			if(cache.shouldRenderGlow){

				float alpha=info.weathereff * (float) (Optics.getAlphaFromMagnitude(14.5+cache.appMag-2.5*Math.log10(cache.difactor),info.bglight));

				Vector3 pos = cache.pos;
				Vector3 dif = cache.dif;
				Vector3 dif2 = cache.dif2;
				
				info.tessellator.startDrawingQuads();
				info.tessellator.setColorRGBA_F((float)cache.color[0], (float)cache.color[1], (float)cache.color[2], alpha*(float)cache.color[3]);
				info.tessellator.addVertexWithUV(pos.getX()+dif.getX(), pos.getY()+dif.getY(), pos.getZ()+dif.getZ(),0.0,0.0);
				info.tessellator.addVertexWithUV(pos.getX()+dif2.getX(), pos.getY()+dif2.getY(), pos.getZ()+dif2.getZ(),0.0,1.0);
				info.tessellator.addVertexWithUV(pos.getX()-dif.getX(), pos.getY()-dif.getY(), pos.getZ()-dif.getZ(),1.0,1.0);
				info.tessellator.addVertexWithUV(pos.getX()-dif2.getX(), pos.getY()-dif2.getY(), pos.getZ()-dif2.getZ(),1.0,0.0);
				info.tessellator.draw();
			}
		} else if(info.pass == EnumRenderPass.OpaqueStellar) {
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
					info.tessellator.setColorRGBA_F((float)cache.color[0] * (1.0f - lightlevel),
							(float)cache.color[1] * (1.0f - lightlevel),
							(float)cache.color[2] * (1.0f - lightlevel), ((info.weathereff*cache.moonilum[longc][latc]-0.015f*info.bglight)*2.0f)*(float)cache.color[3]);
					info.tessellator.setNormal((float)cache.moonnormal[longc][latc].getX(), (float)cache.moonnormal[longc][latc].getY(), (float)cache.moonnormal[longc][latc].getZ());
					info.tessellator.addVertexWithUV(cache.moonPos[longc][latc].getX(), cache.moonPos[longc][latc].getY(), cache.moonPos[longc][latc].getZ(), (longd+0.5)%1.0, latd);

					info.tessellator.setColorRGBA_F((float)cache.color[0] * (1.0f - lightlevel),
							(float)cache.color[1] * (1.0f - lightlevel),
							(float)cache.color[2] * (1.0f - lightlevel), ((info.weathereff*cache.moonilum[longcd][latc]-0.015f*info.bglight)*2.0f)*(float)cache.color[3]);
					info.tessellator.setNormal((float)cache.moonnormal[longcd][latc].getX(), (float)cache.moonnormal[longcd][latc].getY(), (float)cache.moonnormal[longcd][latc].getZ());
					info.tessellator.addVertexWithUV(cache.moonPos[longcd][latc].getX(), cache.moonPos[longcd][latc].getY(), cache.moonPos[longcd][latc].getZ(), (longdd+0.5)%1.0, latd);

					info.tessellator.setColorRGBA_F((float)cache.color[0] * (1.0f - lightlevel),
							(float)cache.color[1] * (1.0f - lightlevel),
							(float)cache.color[2] * (1.0f - lightlevel), ((info.weathereff*cache.moonilum[longcd][latc+1]-0.015f*info.bglight)*2.0f)*(float)cache.color[3]);
					info.tessellator.setNormal((float)cache.moonnormal[longcd][latc+1].getX(), (float)cache.moonnormal[longcd][latc+1].getY(), (float)cache.moonnormal[longcd][latc+1].getZ());
					info.tessellator.addVertexWithUV(cache.moonPos[longcd][latc+1].getX(), cache.moonPos[longcd][latc+1].getY(), cache.moonPos[longcd][latc+1].getZ(), (longdd+0.5)%1.0, latdd);

					info.tessellator.setColorRGBA_F((float)cache.color[0] * (1.0f - lightlevel),
							(float)cache.color[1] * (1.0f - lightlevel),
							(float)cache.color[2] * (1.0f - lightlevel), ((info.weathereff*cache.moonilum[longc][latc+1]-0.015f*info.bglight)*2.0f)*(float)cache.color[3]);
					info.tessellator.setNormal((float)cache.moonnormal[longc][latc+1].getX(), (float)cache.moonnormal[longc][latc+1].getY(), (float)cache.moonnormal[longc][latc+1].getZ());
					info.tessellator.addVertexWithUV(cache.moonPos[longc][latc+1].getX(), cache.moonPos[longc][latc+1].getY(), cache.moonPos[longc][latc+1].getZ(), (longd+0.5)%1.0, latdd);
				}
			}

			GL11.glEnable(GL11.GL_NORMALIZE);
			GL11.glShadeModel(GL11.GL_SMOOTH);
			info.tessellator.draw();
			GL11.glShadeModel(GL11.GL_FLAT);
			GL11.glDisable(GL11.GL_NORMALIZE);
		}
	}

}
