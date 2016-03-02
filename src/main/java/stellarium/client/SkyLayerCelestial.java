package stellarium.client;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import sciapi.api.value.IValRef;
import sciapi.api.value.euclidian.CrossUtil;
import sciapi.api.value.euclidian.EVector;
import sciapi.api.value.euclidian.IEVector;
import sciapi.api.value.util.VOp;
import stellarium.StellarSky;
import stellarium.stellars.Color;
import stellarium.stellars.ExtinctionRefraction;
import stellarium.stellars.Optics;
import stellarium.stellars.StellarObj;
import stellarium.stellars.background.BrStar;
import stellarium.util.math.SpCoord;
import stellarium.util.math.Spmath;
import stellarium.util.math.Transforms;
import stellarium.util.math.VecMath;

public class SkyLayerCelestial implements ISkyRenderLayer {

	private static final ResourceLocation locationSunPng = new ResourceLocation("stellarium", "stellar/halo.png");
	private static final ResourceLocation locationMoonPng = new ResourceLocation("stellarium", "stellar/lune.png");
	private static final ResourceLocation locationStarPng = new ResourceLocation("stellarium", "stellar/star.png");
	private static final ResourceLocation locationhalolunePng = new ResourceLocation("stellarium", "stellar/haloLune.png");
	private static final ResourceLocation locationMilkywayPng = new ResourceLocation("stellarium", "stellar/milkyway.png");

	private EVector dif = new EVector(3);
	private EVector dif2 = new EVector(3);
	
	private Minecraft mc;
	private ClientSettings settings;
	private Random random;
	Tessellator tessellator1 = Tessellator.instance;
	
	public SkyLayerCelestial() {
		this.random = new Random(System.currentTimeMillis());
		this.settings = StellarSky.proxy.getClientSettings();
	}
	
	@Override
	public void render(float partialTicks, WorldClient world, Minecraft mc) {
		Vec3 skyColor = world.getSkyColor(mc.renderViewEntity, partialTicks);
		float f1 = (float)skyColor.xCoord;
		float f2 = (float)skyColor.yCoord;
		float f3 = (float)skyColor.zCoord;
		TextureManager renderEngine = mc.renderEngine;
		this.mc = mc;
		
		GL11.glPushMatrix();
		float f4 = 1.0F - world.getRainStrength(partialTicks);

		float bglight=f1+f2+f3;

		GL11.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f); //e,n,z

		GL11.glColor4f(1.0F, 1.0F, 1.0F, f4);


		double time=(double)world.getWorldTime()+partialTicks;


		this.renderStar(bglight, f4, time);


		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

		GL11.glColor4f(1.0F, 1.0F, 1.0F, f4);

		//Rendering Sun		
		EVector pos = new EVector(3);
		pos.set(StellarSky.getManager().Sun.getPosition());
		double size=StellarSky.getManager().Sun.radius/Spmath.getD(VecMath.size(pos))*99.0*20;
		pos.set(VecMath.normalize(pos));
		dif.set(VOp.normalize(CrossUtil.cross((IEVector)pos, (IEVector)new EVector(0.0,0.0,1.0))));
		dif2.set((IValRef)CrossUtil.cross((IEVector)dif, (IEVector)pos));
		pos.set(VecMath.mult(99.0, pos));

		dif.set(VecMath.mult(size, dif));
		dif2.set(VecMath.mult(size, dif2));

		renderEngine.bindTexture(this.locationSunPng);
		tessellator1.startDrawingQuads();
		tessellator1.addVertexWithUV(VecMath.getX(pos)+VecMath.getX(dif), VecMath.getY(pos)+VecMath.getY(dif), VecMath.getZ(pos)+VecMath.getZ(dif),0.0,0.0);
		tessellator1.addVertexWithUV(VecMath.getX(pos)+VecMath.getX(dif2), VecMath.getY(pos)+VecMath.getY(dif2), VecMath.getZ(pos)+VecMath.getZ(dif2),1.0,0.0);
		tessellator1.addVertexWithUV(VecMath.getX(pos)-VecMath.getX(dif), VecMath.getY(pos)-VecMath.getY(dif), VecMath.getZ(pos)-VecMath.getZ(dif),1.0,1.0);
		tessellator1.addVertexWithUV(VecMath.getX(pos)-VecMath.getX(dif2), VecMath.getY(pos)-VecMath.getY(dif2), VecMath.getZ(pos)-VecMath.getZ(dif2),0.0,1.0);
		tessellator1.draw();
		//Sun


		//Rendering Moon
		EVector posm = new EVector(3);

		posm.set(ExtinctionRefraction.refraction(StellarSky.getManager().Moon.getPosition(), true));
		double sizem=StellarSky.getManager().Moon.radius.asDouble()/Spmath.getD(VecMath.size(posm));

		double difactor = 0.8 / 180.0 * Math.PI / sizem;
		difactor = difactor * difactor / Math.PI;

		sizem *= (98.0*5.0);

		int latn = settings.imgFrac, longn=2*settings.imgFrac;
		EVector moonvec[][], moonnormal[][];
		float moonilum[][];
		moonvec=new EVector[longn][latn+1];
		moonilum=new float[longn][latn+1];
		moonnormal=new EVector[longn][latn+1];
		EVector Buf = new EVector(3);
		EVector Buff = new EVector(3);
		int latc, longc;
		for(longc=0; longc<longn; longc++){
			for(latc=0; latc<=latn; latc++){
				Buf.set(StellarSky.getManager().Moon.posLocalM((double)longc/(double)longn*360.0, (double)latc/(double)latn*180.0-90.0, Transforms.yr));
				moonilum[longc][latc]=(float) (StellarSky.getManager().Moon.illumination(Buf) * difactor * 1.5);
				moonnormal[longc][latc] = new EVector(3).set(Buf);
				Buf.set(StellarSky.getManager().Moon.posLocalG(Buf));
				Buf.set(VecMath.mult(50000.0, Buf));
				Buff.set(VecMath.getX(Buf),VecMath.getY(Buf),VecMath.getZ(Buf));
				IValRef ref=Transforms.ZTEctoNEc.transform((IEVector)Buff);
				ref=Transforms.EctoEq.transform(ref);
				ref=Transforms.NEqtoREq.transform(ref);
				ref=Transforms.REqtoHor.transform(ref);

				moonvec[longc][latc] = new EVector(3);
				moonvec[longc][latc].set(ExtinctionRefraction.refraction(ref, true));

				if(VecMath.getZ(moonvec[longc][latc])<0.0f) moonilum[longc][latc]=0.0f;
			}
		}

		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

		renderEngine.bindTexture(locationhalolunePng);

		if(VecMath.getZ(posm)>0.0f){

			posm.set(VOp.normalize(posm));
			difm.set(VOp.normalize(CrossUtil.cross((IEVector)posm, (IEVector)new EVector(0.0,0.0,1.0))));
			difm2.set((IValRef)CrossUtil.cross((IEVector)difm, (IEVector)posm));
			posm.set(VecMath.mult(98.0, posm));

			difm.set(VecMath.mult(sizem, difm));
			difm2.set(VecMath.mult(sizem, difm2));

			float alpha=(float) (Optics.getAlphaFromMagnitude(-17.0-StellarSky.getManager().Moon.mag-2.5*Math.log10(difactor),bglight) / (StellarSky.getManager().Moon.getPhase()));		
			
			GL11.glColor4d(1.0, 1.0, 1.0, f4*alpha);

			tessellator1.startDrawingQuads();
			tessellator1.addVertexWithUV(VecMath.getX(posm)+VecMath.getX(difm), VecMath.getY(posm)+VecMath.getY(difm), VecMath.getZ(posm)+VecMath.getZ(difm),0.0,0.0);
			tessellator1.addVertexWithUV(VecMath.getX(posm)+VecMath.getX(difm2), VecMath.getY(posm)+VecMath.getY(difm2), VecMath.getZ(posm)+VecMath.getZ(difm2),0.0,1.0);
			tessellator1.addVertexWithUV(VecMath.getX(posm)-VecMath.getX(difm), VecMath.getY(posm)-VecMath.getY(difm), VecMath.getZ(posm)-VecMath.getZ(difm),1.0,1.0);
			tessellator1.addVertexWithUV(VecMath.getX(posm)-VecMath.getX(difm2), VecMath.getY(posm)-VecMath.getY(difm2), VecMath.getZ(posm)-VecMath.getZ(difm2),1.0,0.0);
			tessellator1.draw();
		}


		renderEngine.bindTexture(locationMoonPng);

		tessellator1.startDrawingQuads();

		for(longc=0; longc<longn; longc++){
			for(latc=0; latc<latn; latc++){

				int longcd=(longc+1)%longn;
				double longd=(double)longc/(double)longn;
				double latd=1.0-(double)latc/(double)latn;
				double longdd=(double)(longc+1)/(double)longn;
				double latdd=1.0-(double)(latc+1)/(double)latn;

				float lightlevel = (0.875f*(bglight/2.1333334f));
				tessellator1.setColorRGBA_F(1.0f - lightlevel, 1.0f - lightlevel, 1.0f - lightlevel, ((f4*moonilum[longc][latc]-0.015f*bglight)*2.0f));
				
				tessellator1.setNormal((float)VecMath.getX(moonnormal[longc][latc]), (float)VecMath.getY(moonnormal[longc][latc]), (float)VecMath.getZ(moonnormal[longc][latc]));
				tessellator1.addVertexWithUV(VecMath.getX(moonvec[longc][latc]), VecMath.getY(moonvec[longc][latc]), VecMath.getZ(moonvec[longc][latc]), Spmath.fmod(longd+0.5, 1.0), latd);
				
				tessellator1.setNormal((float)VecMath.getX(moonnormal[longcd][latc]), (float)VecMath.getY(moonnormal[longcd][latc]), (float)VecMath.getZ(moonnormal[longcd][latc]));
				tessellator1.addVertexWithUV(VecMath.getX(moonvec[longcd][latc]), VecMath.getY(moonvec[longcd][latc]), VecMath.getZ(moonvec[longcd][latc]), Spmath.fmod(longdd+0.5, 1.0), latd);
				
				tessellator1.setNormal((float)VecMath.getX(moonnormal[longcd][latc+1]), (float)VecMath.getY(moonnormal[longcd][latc+1]), (float)VecMath.getZ(moonnormal[longcd][latc+1]));
				tessellator1.addVertexWithUV(VecMath.getX(moonvec[longcd][latc+1]), VecMath.getY(moonvec[longcd][latc+1]), VecMath.getZ(moonvec[longcd][latc+1]), Spmath.fmod(longdd+0.5, 1.0), latdd);
				
				tessellator1.setNormal((float)VecMath.getX(moonnormal[longc][latc+1]), (float)VecMath.getY(moonnormal[longc][latc+1]), (float)VecMath.getZ(moonnormal[longc][latc+1]));
				tessellator1.addVertexWithUV(VecMath.getX(moonvec[longc][latc+1]), VecMath.getY(moonvec[longc][latc+1]), VecMath.getZ(moonvec[longc][latc+1]), Spmath.fmod(longd+0.5, 1.0), latdd);
			}
		}

		tessellator1.draw();
		//Moon
		
		//Rendering galaxy
		latn = settings.imgFracMilkyway;
		longn=2*settings.imgFracMilkyway;
		moonvec=new EVector[longn][latn+1];
		
		for(longc=0; longc<longn; longc++){
			for(latc=0; latc<=latn; latc++){
				Buf.set(new SpCoord(longc*360.0/longn + 90.0, latc*180.0/latn - 90.0).getVec());
				Buf.set(VecMath.mult(50.0, Buf));
				IValRef ref=Transforms.EqtoEc.transform(Buf);
				ref=Transforms.ZTEctoNEc.transform(ref);
				ref=Transforms.EctoEq.transform(ref);
				ref=Transforms.NEqtoREq.transform(ref);
				ref=Transforms.REqtoHor.transform(ref);

				moonvec[longc][latc] = new EVector(3);
				moonvec[longc][latc].set(ExtinctionRefraction.refraction(ref, true));
			}
		}
		
		renderEngine.bindTexture(locationMilkywayPng);
		tessellator1.startDrawingQuads();
		
		float Mag = 3.5f;
		float alpha=Optics.getAlphaForGalaxy(Mag, bglight) - (((1-f4)/1)*20f);
		
		for(longc=0; longc<longn; longc++){
			for(latc=0; latc<latn; latc++){
				int longcd=(longc+1)%longn;
				double longd=1.0-(double)longc/(double)longn;
				double latd=1.0-(double)latc/(double)latn;
				double longdd=1.0-(double)(longc+1)/(double)longn;
				double latdd=1.0-(double)(latc+1)/(double)latn;

				tessellator1.setColorRGBA_F(1.0f, 1.0f, 1.0f, settings.milkywayBrightness * alpha);
				tessellator1.addVertexWithUV(VecMath.getX(moonvec[longc][latc]), VecMath.getY(moonvec[longc][latc]), VecMath.getZ(moonvec[longc][latc]), longd, latd);
				tessellator1.addVertexWithUV(VecMath.getX(moonvec[longc][latc+1]), VecMath.getY(moonvec[longc][latc+1]), VecMath.getZ(moonvec[longc][latc+1]), longd, latdd);
				tessellator1.addVertexWithUV(VecMath.getX(moonvec[longcd][latc+1]), VecMath.getY(moonvec[longcd][latc+1]), VecMath.getZ(moonvec[longcd][latc+1]), longdd, latdd);
				tessellator1.addVertexWithUV(VecMath.getX(moonvec[longcd][latc]), VecMath.getY(moonvec[longcd][latc]), VecMath.getZ(moonvec[longcd][latc]), longdd, latd);
			}
		}
		tessellator1.draw();
		//galaxy

		//Rendering stellar objects
		renderEngine.bindTexture(locationStarPng);
		for(StellarObj object : StellarSky.getManager().getPlanets()) {
			this.drawStellarObj(bglight, f4, object);
		}

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		float f18 = world.getStarBrightness(partialTicks);


		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_FOG);
		GL11.glPopMatrix();
	}
	
	public void renderStar(float bglight, float weathereff, double time){

		GL11.glEnable(GL11.GL_COLOR_MATERIAL);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

		mc.renderEngine.bindTexture(locationStarPng);

		EVector pos = new EVector(3);

		tessellator1.startDrawingQuads();
		for(int i=0; i<BrStar.NumStar; i++){
			if(BrStar.stars[i].unable)
				continue;

			BrStar star=BrStar.stars[i];

			pos.set(VecMath.normalize(star.appPos));
			float Mag=star.App_Mag;
			float B_V=star.App_B_V;

			if(Mag > settings.mag_Limit)
				continue;

			if(VecMath.getZ(pos)<0) continue;

			float size=0.5f;
			float alpha=Optics.getAlphaFromMagnitudeSparkling(Mag, bglight) - (((1-weathereff)/1)*20f);

			dif.set(CrossUtil.cross(pos, new EVector(0.0,0.0,1.0)));
			if(Spmath.getD(VecMath.size2(dif)) < 0.01)
				dif.set(CrossUtil.cross(pos, new EVector(0.0,1.0,0.0)));
			dif.set(VecMath.normalize(dif));
			dif2.set((IValRef)CrossUtil.cross(dif, pos));
			pos.set(VecMath.mult(100.0, pos));

			dif.set(VecMath.mult(size, dif));
			dif2.set(VecMath.mult(size, dif2));

			Color c=Color.getColor(B_V);

			tessellator1.setColorRGBA(c.r, c.g, c.b, (int)(alpha*255.0));
			tessellator1.addVertexWithUV(VecMath.getX(pos)+VecMath.getX(dif), VecMath.getY(pos)+VecMath.getY(dif), VecMath.getZ(pos)+VecMath.getZ(dif),0.0,0.0);
			tessellator1.addVertexWithUV(VecMath.getX(pos)+VecMath.getX(dif2), VecMath.getY(pos)+VecMath.getY(dif2), VecMath.getZ(pos)+VecMath.getZ(dif2),1.0,0.0);
			tessellator1.addVertexWithUV(VecMath.getX(pos)-VecMath.getX(dif), VecMath.getY(pos)-VecMath.getY(dif), VecMath.getZ(pos)-VecMath.getZ(dif),1.0,1.0);
			tessellator1.addVertexWithUV(VecMath.getX(pos)-VecMath.getX(dif2), VecMath.getY(pos)-VecMath.getY(dif2), VecMath.getZ(pos)-VecMath.getZ(dif2),0.0,1.0);
		}

		tessellator1.draw();

	}


	EVector difm = new EVector(3);
	EVector difm2 = new EVector(3);

	public void drawStellarObj(float bglight, float weathereff, StellarObj object) {
		this.drawStellarObj(bglight, weathereff, object.appPos, object.appMag);
	}

	public void drawStellarObj(float bglight, float weathereff, EVector pos, double Mag) {

		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

		if(Mag > settings.mag_Limit) return;
		if(VecMath.getZ(pos)<0) return;

		float size=0.6f;
		float alpha=Optics.getAlphaFromMagnitude(Mag, bglight) - (((1-weathereff)/1)*20f);

		pos.set(VecMath.normalize(pos));

		difm.set(CrossUtil.cross((IEVector)pos, (IEVector)new EVector(0.0,0.0,1.0)));
		if(Spmath.getD(VecMath.size2(difm)) < 0.01)
			difm.set(CrossUtil.cross((IEVector)pos, (IEVector)new EVector(0.0,1.0,0.0)));
		difm.set(VecMath.normalize(difm));
		difm2.set((IValRef)CrossUtil.cross((IEVector)difm, (IEVector)pos));
		pos.set(VecMath.mult(99.0, pos));

		difm.set(VecMath.mult(size, difm));
		difm2.set(VecMath.mult(size, difm2));

		tessellator1.startDrawingQuads();

		tessellator1.setColorRGBA_F(1.0f, 1.0f, 1.0f, alpha);

		tessellator1.addVertexWithUV(VecMath.getX(pos)+VecMath.getX(difm), VecMath.getY(pos)+VecMath.getY(difm), VecMath.getZ(pos)+VecMath.getZ(difm),0.0,0.0);
		tessellator1.addVertexWithUV(VecMath.getX(pos)+VecMath.getX(difm2), VecMath.getY(pos)+VecMath.getY(difm2), VecMath.getZ(pos)+VecMath.getZ(difm2),1.0,0.0);
		tessellator1.addVertexWithUV(VecMath.getX(pos)-VecMath.getX(difm), VecMath.getY(pos)-VecMath.getY(difm), VecMath.getZ(pos)-VecMath.getZ(difm),1.0,1.0);
		tessellator1.addVertexWithUV(VecMath.getX(pos)-VecMath.getX(difm2), VecMath.getY(pos)-VecMath.getY(difm2), VecMath.getZ(pos)-VecMath.getZ(difm2),0.0,1.0);

		tessellator1.draw();
	}

}
