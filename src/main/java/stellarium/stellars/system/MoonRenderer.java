package stellarium.stellars.system;

import org.lwjgl.opengl.GL11;

import net.minecraft.util.ResourceLocation;
import sciapi.api.value.euclidian.EVector;
import stellarium.api.PerDimensionResource;
import stellarium.render.ICelestialObjectRenderer;
import stellarium.render.StellarRenderInfo;
import stellarium.stellars.Optics;
import stellarium.util.math.SpCoord;
import stellarium.util.math.Spmath;
import stellarium.util.math.VecMath;

public class MoonRenderer implements ICelestialObjectRenderer<MoonRenderCache> {
	
	private static final PerDimensionResource resourceMoonSurface = 
			new PerDimensionResource("Moon_Surface", new ResourceLocation("stellarium", "stellar/lune.png"));
	private static final PerDimensionResource resourceMoonHalo =
			new PerDimensionResource("Moon_Halo", new ResourceLocation("stellarium", "stellar/haloLune.png"));
	
	@Override
	public void render(StellarRenderInfo info, MoonRenderCache cache) {
		
		info.mc.renderEngine.bindTexture(resourceMoonHalo.getLocationFor(info.mc.theWorld));

		if(cache.shouldRenderGlow){
			EVector pos = cache.appCoord.getVec();
			EVector dif = new SpCoord(cache.appCoord.x+90, 0.0).getVec();
			EVector dif2 = new SpCoord(cache.appCoord.x, cache.appCoord.y+90).getVec();
			
			pos.set(VecMath.mult(98.0, pos));
			dif.set(VecMath.mult(cache.size, dif));
			dif2.set(VecMath.mult(-cache.size, dif2));
			
			float alpha=(float) (Optics.getAlphaFromMagnitude(14.5+cache.appMag-2.5*Math.log10(cache.difactor),info.bglight));
			
			GL11.glColor4d(1.0, 1.0, 1.0, info.weathereff*alpha);

			info.tessellator.startDrawingQuads();
			info.tessellator.addVertexWithUV(VecMath.getX(pos)+VecMath.getX(dif), VecMath.getY(pos)+VecMath.getY(dif), VecMath.getZ(pos)+VecMath.getZ(dif),0.0,0.0);
			info.tessellator.addVertexWithUV(VecMath.getX(pos)+VecMath.getX(dif2), VecMath.getY(pos)+VecMath.getY(dif2), VecMath.getZ(pos)+VecMath.getZ(dif2),0.0,1.0);
			info.tessellator.addVertexWithUV(VecMath.getX(pos)-VecMath.getX(dif), VecMath.getY(pos)-VecMath.getY(dif), VecMath.getZ(pos)-VecMath.getZ(dif),1.0,1.0);
			info.tessellator.addVertexWithUV(VecMath.getX(pos)-VecMath.getX(dif2), VecMath.getY(pos)-VecMath.getY(dif2), VecMath.getZ(pos)-VecMath.getZ(dif2),1.0,0.0);
			info.tessellator.draw();
		}


		info.mc.renderEngine.bindTexture(resourceMoonSurface.getLocationFor(info.mc.theWorld));

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
				
				info.tessellator.setNormal((float)VecMath.getX(cache.moonnormal[longc][latc]), (float)VecMath.getY(cache.moonnormal[longc][latc]), (float)VecMath.getZ(cache.moonnormal[longc][latc]));
				info.tessellator.addVertexWithUV(VecMath.getX(cache.moonPos[longc][latc]), VecMath.getY(cache.moonPos[longc][latc]), VecMath.getZ(cache.moonPos[longc][latc]), Spmath.fmod(longd+0.5, 1.0), latd);
				
				info.tessellator.setNormal((float)VecMath.getX(cache.moonnormal[longcd][latc]), (float)VecMath.getY(cache.moonnormal[longcd][latc]), (float)VecMath.getZ(cache.moonnormal[longcd][latc]));
				info.tessellator.addVertexWithUV(VecMath.getX(cache.moonPos[longcd][latc]), VecMath.getY(cache.moonPos[longcd][latc]), VecMath.getZ(cache.moonPos[longcd][latc]), Spmath.fmod(longdd+0.5, 1.0), latd);
				
				info.tessellator.setNormal((float)VecMath.getX(cache.moonnormal[longcd][latc+1]), (float)VecMath.getY(cache.moonnormal[longcd][latc+1]), (float)VecMath.getZ(cache.moonnormal[longcd][latc+1]));
				info.tessellator.addVertexWithUV(VecMath.getX(cache.moonPos[longcd][latc+1]), VecMath.getY(cache.moonPos[longcd][latc+1]), VecMath.getZ(cache.moonPos[longcd][latc+1]), Spmath.fmod(longdd+0.5, 1.0), latdd);
				
				info.tessellator.setNormal((float)VecMath.getX(cache.moonnormal[longc][latc+1]), (float)VecMath.getY(cache.moonnormal[longc][latc+1]), (float)VecMath.getZ(cache.moonnormal[longc][latc+1]));
				info.tessellator.addVertexWithUV(VecMath.getX(cache.moonPos[longc][latc+1]), VecMath.getY(cache.moonPos[longc][latc+1]), VecMath.getZ(cache.moonPos[longc][latc+1]), Spmath.fmod(longd+0.5, 1.0), latdd);
			}
		}

		info.tessellator.draw();
	}

}
