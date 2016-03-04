package stellarium.client;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import sciapi.api.value.IValRef;
import sciapi.api.value.euclidian.CrossUtil;
import sciapi.api.value.euclidian.EVector;
import sciapi.api.value.euclidian.IEVector;
import sciapi.api.value.util.VOp;
import stellarium.StellarSky;
import stellarium.stellars.ExtinctionRefraction;
import stellarium.stellars.Optics;
import stellarium.util.math.Spmath;
import stellarium.util.math.Transforms;
import stellarium.util.math.VecMath;

public class CelestialLayerMoon implements ICelestialLayer {
	
	private static final ResourceLocation locationMoonPng = new ResourceLocation("stellarium", "stellar/lune.png");
	private static final ResourceLocation locationhalolunePng = new ResourceLocation("stellarium", "stellar/haloLune.png");
	
	private EVector posm = new EVector(3), difm = new EVector(3), difm2 = new EVector(3);
	private int latn, longn;
	private EVector moonvec[][], moonnormal[][];
	private float moonilum[][];

	EVector Buf = new EVector(3);
	EVector Buff = new EVector(3);
	
	@Override
	public void init(ClientSettings settings) {
		this.latn = settings.imgFrac;
		this.longn = 2*settings.imgFrac;
		this.moonvec=new EVector[longn][latn+1];
		this.moonilum=new float[longn][latn+1];
		this.moonnormal=new EVector[longn][latn+1];
	}
	
	@Override
	public void render(Minecraft mc, float bglight, float weathereff, double time) {
		
		posm.set(ExtinctionRefraction.refraction(StellarSky.getManager().Moon.getPosition(), true));
		double sizem=StellarSky.getManager().Moon.radius.asDouble()/Spmath.getD(VecMath.size(posm));

		double difactor = 0.8 / 180.0 * Math.PI / sizem;
		difactor = difactor * difactor / Math.PI;

		sizem *= (98.0*5.0);
		
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

		Tessellator tessellator1 = Tessellator.instance;
		
		mc.renderEngine.bindTexture(locationhalolunePng);

		if(VecMath.getZ(posm)>0.0f){

			posm.set(VOp.normalize(posm));
			difm.set(VOp.normalize(CrossUtil.cross((IEVector)posm, (IEVector)new EVector(0.0,0.0,1.0))));
			difm2.set((IValRef)CrossUtil.cross((IEVector)difm, (IEVector)posm));
			posm.set(VecMath.mult(98.0, posm));

			difm.set(VecMath.mult(sizem, difm));
			difm2.set(VecMath.mult(sizem, difm2));

			float alpha=(float) (Optics.getAlphaFromMagnitude(-17.0-StellarSky.getManager().Moon.mag-2.5*Math.log10(difactor),bglight) / (StellarSky.getManager().Moon.getPhase()));		
			
			GL11.glColor4d(1.0, 1.0, 1.0, weathereff*alpha);

			tessellator1.startDrawingQuads();
			tessellator1.addVertexWithUV(VecMath.getX(posm)+VecMath.getX(difm), VecMath.getY(posm)+VecMath.getY(difm), VecMath.getZ(posm)+VecMath.getZ(difm),0.0,0.0);
			tessellator1.addVertexWithUV(VecMath.getX(posm)+VecMath.getX(difm2), VecMath.getY(posm)+VecMath.getY(difm2), VecMath.getZ(posm)+VecMath.getZ(difm2),0.0,1.0);
			tessellator1.addVertexWithUV(VecMath.getX(posm)-VecMath.getX(difm), VecMath.getY(posm)-VecMath.getY(difm), VecMath.getZ(posm)-VecMath.getZ(difm),1.0,1.0);
			tessellator1.addVertexWithUV(VecMath.getX(posm)-VecMath.getX(difm2), VecMath.getY(posm)-VecMath.getY(difm2), VecMath.getZ(posm)-VecMath.getZ(difm2),1.0,0.0);
			tessellator1.draw();
		}


		mc.renderEngine.bindTexture(locationMoonPng);

		tessellator1.startDrawingQuads();

		for(longc=0; longc<longn; longc++){
			for(latc=0; latc<latn; latc++){

				int longcd=(longc+1)%longn;
				double longd=(double)longc/(double)longn;
				double latd=1.0-(double)latc/(double)latn;
				double longdd=(double)(longc+1)/(double)longn;
				double latdd=1.0-(double)(latc+1)/(double)latn;

				float lightlevel = (0.875f*(bglight/2.1333334f));
				tessellator1.setColorRGBA_F(1.0f - lightlevel, 1.0f - lightlevel, 1.0f - lightlevel, ((weathereff*moonilum[longc][latc]-0.015f*bglight)*2.0f));
				
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
	}

}
