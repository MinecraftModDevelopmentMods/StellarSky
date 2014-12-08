package stellarium;


import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.*;

import org.lwjgl.opengl.*;

import sciapi.api.value.IValRef;
import sciapi.api.value.euclidian.CrossUtil;
import sciapi.api.value.euclidian.EVector;
import sciapi.api.value.euclidian.IEVector;
import sciapi.api.value.numerics.DFloatSet;
import sciapi.api.value.util.VOp;
import stellarium.stellars.*;
import stellarium.stellars.background.BrStar;
import stellarium.util.math.Spmath;
import stellarium.util.math.Transforms;
import stellarium.util.math.VecMath;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class DrawSky extends IRenderHandler {
	
	private TextureManager renderEngine;
	private WorldClient world;
	private Minecraft mc;
	private Random random;
	private Tessellator tessellator1=Tessellator.instance;
	
    private static final ResourceLocation locationEndSkyPng = new ResourceLocation("textures/environment/end_sky.png");
    private static final ResourceLocation locationSunPng = new ResourceLocation("stellarium", "stellar/halo.png");
    private static final ResourceLocation locationMoonPng = new ResourceLocation("stellarium", "stellar/lune.png");
    private static final ResourceLocation locationStarPng = new ResourceLocation("stellarium", "stellar/star.png");
    private static final ResourceLocation locationhalolunePng = new ResourceLocation("stellarium", "stellar/haloLune.png");

    
	/*private boolean IsMid, IsCalcd;*/
	
	public DrawSky(){
		random = new Random(System.currentTimeMillis());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void render(float par1, WorldClient world, Minecraft mc) {
		
		this.renderEngine=mc.renderEngine;
		this.world=world;
		this.mc=mc;
		
		if (mc.theWorld.provider.dimensionId == 1)
        {
            GL11.glDisable(GL11.GL_FOG);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            RenderHelper.disableStandardItemLighting();
            GL11.glDepthMask(false);
            renderEngine.bindTexture(locationEndSkyPng);
            Tessellator tessellator = Tessellator.instance;

            for (int i = 0; i < 6; ++i)
            {
                GL11.glPushMatrix();

                if (i == 1)
                {
                    GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
                }

                if (i == 2)
                {
                    GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                }

                if (i == 3)
                {
                    GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
                }

                if (i == 4)
                {
                    GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
                }

                if (i == 5)
                {
                    GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
                }

                tessellator.startDrawingQuads();
                tessellator.setColorOpaque_I(2631720);
                tessellator.addVertexWithUV(-100.0D, -100.0D, -100.0D, 0.0D, 0.0D);
                tessellator.addVertexWithUV(-100.0D, -100.0D, 100.0D, 0.0D, 16.0D);
                tessellator.addVertexWithUV(100.0D, -100.0D, 100.0D, 16.0D, 16.0D);
                tessellator.addVertexWithUV(100.0D, -100.0D, -100.0D, 16.0D, 0.0D);
                tessellator.draw();
                GL11.glPopMatrix();
            }

            GL11.glDepthMask(true);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            
            double time=(double)world.getWorldTime()+par1;
            
            this.RenderStar(0.0f, 0.0f, time);
        }
        else if (mc.theWorld.provider.isSurfaceWorld())
        {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            Vec3 vec3 = world.getSkyColor(mc.renderViewEntity, par1);
            float f1 = (float)vec3.xCoord;
            float f2 = (float)vec3.yCoord;
            float f3 = (float)vec3.zCoord;
            float f4;

            if (mc.gameSettings.anaglyph)
            {
                float f5 = (f1 * 30.0F + f2 * 59.0F + f3 * 11.0F) / 100.0F;
                float f6 = (f1 * 30.0F + f2 * 70.0F) / 100.0F;
                f4 = (f1 * 30.0F + f3 * 70.0F) / 100.0F;
                f1 = f5;
                f2 = f6;
                f3 = f4;
            }

            GL11.glColor3f(f1, f2, f3);
            Tessellator tessellator1 = Tessellator.instance;
            GL11.glDepthMask(false);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            RenderHelper.disableStandardItemLighting();
            float[] afloat = world.provider.calcSunriseSunsetColors(world.getCelestialAngle(par1), par1);
            float f7;
            float f8;
            float f9;
            float f10;

            if (afloat != null)
            {
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glShadeModel(GL11.GL_SMOOTH);
                GL11.glPushMatrix();
                GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(MathHelper.sin(world.getCelestialAngleRadians(par1)) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
                f4 = afloat[0];
                f7 = afloat[1];
                f8 = afloat[2];
                float f11;

                if (mc.gameSettings.anaglyph)
                {
                    f9 = (f4 * 30.0F + f7 * 59.0F + f8 * 11.0F) / 100.0F;
                    f10 = (f4 * 30.0F + f7 * 70.0F) / 100.0F;
                    f11 = (f4 * 30.0F + f8 * 70.0F) / 100.0F;
                    f4 = f9;
                    f7 = f10;
                    f8 = f11;
                }

                tessellator1.startDrawing(6);
                tessellator1.setColorRGBA_F(f4, f7, f8, afloat[3]);
                tessellator1.addVertex(0.0D, 100.0D, 0.0D);
                byte b0 = 16;
                tessellator1.setColorRGBA_F(afloat[0], afloat[1], afloat[2], 0.0F);

                for (int j = 0; j <= b0; ++j)
                {
                    f11 = (float)j * (float)Math.PI * 2.0F / (float)b0;
                    float f12 = MathHelper.sin(f11);
                    float f13 = MathHelper.cos(f11);
                    tessellator1.addVertex((double)(f12 * 120.0F), (double)(f13 * 120.0F), (double)(-f13 * 40.0F * afloat[3]));
                }

                tessellator1.draw();
                GL11.glPopMatrix();
                GL11.glShadeModel(GL11.GL_FLAT);
            }

            GL11.glEnable(GL11.GL_TEXTURE_2D);
            
            GL11.glPushMatrix();
            f4 = 1.0F - world.getRainStrength(par1);
            f7 = 0.0F;
            f8 = 0.0F;
            f9 = 0.0F;
            
            float bglight=f1+f2+f3;
            
           
            GL11.glTranslatef(f7, f8, f9); //e,z,s
            GL11.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f); //e,n,z
//            GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F); // s,z,w
            
            GL11.glColor4f(1.0F, 1.0F, 1.0F, f4);
            
            
            double time=(double)world.getWorldTime()+par1;            
            
            /*if(par1>0.5 && !IsMid){
            	IsMid=true;
            	IsCalcd=false;
            }
            
            if(StellarManager.Earth.EcRPos==null || !IsCalcd)
            {
            	StellarManager.Update(time, mc.theWorld.provider.isSurfaceWorld());
            	IsCalcd=true;
            }*/
                       
            
            this.RenderStar(bglight, f4, time);
            
            
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
            
            GL11.glColor4f(1.0F, 1.0F, 1.0F, f4);
           
           /* GL11.glRotatef(-(float)(90-(Transforms.Lat*180.0/Math.PI)), 1.0f, 0.0f, 0.0f);
            GL11.glRotatef((float)(-90.0-Transforms.Rot*time*180.0/Math.PI), 0.0f, 0.0f, 1.0f);//?,Sp,p
            GL11.glRotatef(-(float) (Transforms.e*180.0/Math.PI), 0.0f, 1.0f, 0.0f);
    		GL11.glRotatef((float)(Transforms.Prec*time*180.0/Math.PI), 0.0f, 0.0f, 1.0f);
            GL11.glRotatef((float) (Transforms.e*180.0/Math.PI), 0.0f, 1.0f, 0.0f);*/

//            GL11.glRotatef(world.getCelestialAngle(par1) * 360.0F, 1.0F, 0.0F, 0.0F);
            
            
            //Rendering Sun
            f10 = 30.0F;
 
            EVector pos = new EVector(3);
            pos.set(StellarManager.Sun.GetPosition());
            double size=StellarManager.Sun.Radius/Spmath.getD(VecMath.size(pos))*99.0*20;
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
/*            tessellator1.addVertexWithUV((double)(-f10), 100.0D, (double)(-f10), 0.0D, 0.0D);
            tessellator1.addVertexWithUV((double)f10, 100.0D, (double)(-f10), 1.0D, 0.0D);
            tessellator1.addVertexWithUV((double)f10, 100.0D, (double)f10, 1.0D, 1.0D);
            tessellator1.addVertexWithUV((double)(-f10), 100.0D, (double)f10, 0.0D, 1.0D);*/
            tessellator1.draw();
            //Sun
          
            
            //Rendering Moon
            
            int latn=StellarManager.ImgFrac, longn=2*StellarManager.ImgFrac;
            EVector moonvec[][];
            double moonilum[][];
            moonvec=new EVector[longn][latn+1];
            moonilum=new double[longn][latn+1];
            EVector Buf = new EVector(3);
            EVector Buff = new EVector(3);
            int latc, longc;
            for(longc=0; longc<longn; longc++){
            	for(latc=0; latc<=latn; latc++){
            		Buf.set(StellarManager.Moon.PosLocalM((double)longc/(double)longn*360.0, (double)latc/(double)latn*180.0-90.0, Transforms.time));
            		moonilum[longc][latc]=StellarManager.Moon.Illumination(Buf);
            		Buf.set(StellarManager.Moon.PosLocalG(Buf));
            		Buf.set(VecMath.mult(50000.0, Buf));
            		Buff.set(VecMath.getX(Buf),VecMath.getY(Buf),VecMath.getZ(Buf));
            		IValRef ref=Transforms.ZTEctoNEc.transform((IEVector)Buff);
            		ref=Transforms.EctoEq.transform(ref);
            		ref=Transforms.NEqtoREq.transform(ref);
            		ref=Transforms.REqtoHor.transform(ref);
            		
            		moonvec[longc][latc] = new EVector(3);
            		moonvec[longc][latc].set(ExtinctionRefraction.Refraction(ref, true));
            		
            		 if(VecMath.getZ(moonvec[longc][latc])<0.0f) moonilum[longc][latc]=0.0;

            	}
            }
          
            
            
            
            f10 = 20.0F;
            
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
            
            renderEngine.bindTexture(locationhalolunePng);
            /*int k = world.getMoonPhase();
            int l = k % 4;
            int i1 = k / 4 % 2;
            float f14 = (float)(l + 0) / 4.0F;
            float f15 = (float)(i1 + 0) / 2.0F;
            float f16 = (float)(l + 1) / 4.0F;
            float f17 = (float)(i1 + 1) / 2.0F;*/
          
            
            EVector posm = new EVector(3);
            
            
            posm.set(ExtinctionRefraction.Refraction(StellarManager.Moon.GetPosition(), true));
            
            if(VecMath.getZ(posm)>0.0f){
            double sizem=StellarManager.Moon.Radius.asDouble()/Spmath.getD(VecMath.size(posm))*98.0*5.0;
           
            posm.set(VOp.normalize(posm));
        	difm.set(VOp.normalize(CrossUtil.cross((IEVector)posm, (IEVector)new EVector(0.0,0.0,1.0))));
        	difm2.set((IValRef)CrossUtil.cross((IEVector)difm, (IEVector)posm));
        	posm.set(VecMath.mult(98.0, posm));
        	
        	difm.set(VecMath.mult(sizem, difm));
        	difm2.set(VecMath.mult(sizem, difm2));
        	
        	float alpha=Optics.GetAlphaFromMagnitude(-17.0-StellarManager.Moon.Mag,bglight);
        	
            GL11.glColor4d(1.0, 1.0, 1.0, f4*alpha);
            
            tessellator1.startDrawingQuads();
        	tessellator1.addVertexWithUV(VecMath.getX(posm)+VecMath.getX(difm), VecMath.getY(posm)+VecMath.getY(difm), VecMath.getZ(posm)+VecMath.getZ(difm),0.0,0.0);
        	tessellator1.addVertexWithUV(VecMath.getX(posm)+VecMath.getX(difm2), VecMath.getY(posm)+VecMath.getY(difm2), VecMath.getZ(posm)+VecMath.getZ(difm2),0.0,1.0);
        	tessellator1.addVertexWithUV(VecMath.getX(posm)-VecMath.getX(difm), VecMath.getY(posm)-VecMath.getY(difm), VecMath.getZ(posm)-VecMath.getZ(difm),1.0,1.0);
        	tessellator1.addVertexWithUV(VecMath.getX(posm)-VecMath.getX(difm2), VecMath.getY(posm)-VecMath.getY(difm2), VecMath.getZ(posm)-VecMath.getZ(difm2),1.0,0.0);
        	tessellator1.draw();
            }
        	
        	
            renderEngine.bindTexture(locationMoonPng);
            
            
            for(longc=0; longc<longn; longc++){
            	for(latc=0; latc<latn; latc++){
            		
            		int longcd=(longc+1)%longn;
            		double longd=(double)longc/(double)longn;
            		double latd=1.0-(double)latc/(double)latn;
            		double longdd=(double)longcd/(double)longn;
            		double latdd=1.0-(double)(latc+1)/(double)latn;
            		
                    GL11.glColor4d(1.0, 1.0, 1.0, (f4*moonilum[longc][latc]-4.0f*bglight)*2.0f);
                	
                    tessellator1.startDrawingQuads();
                    tessellator1.addVertexWithUV(VecMath.getX(moonvec[longc][latc]), VecMath.getY(moonvec[longc][latc]), VecMath.getZ(moonvec[longc][latc]), Spmath.fmod(longd+0.5, 1.0), latd);
                	tessellator1.addVertexWithUV(VecMath.getX(moonvec[longcd][latc]), VecMath.getY(moonvec[longcd][latc]), VecMath.getZ(moonvec[longcd][latc]), Spmath.fmod(longdd+0.5,1.0), latd);
                	tessellator1.addVertexWithUV(VecMath.getX(moonvec[longcd][latc+1]), VecMath.getY(moonvec[longcd][latc+1]), VecMath.getZ(moonvec[longcd][latc+1]), Spmath.fmod(longdd+0.5, 1.0), latdd);
                	tessellator1.addVertexWithUV(VecMath.getX(moonvec[longc][latc+1]), VecMath.getY(moonvec[longc][latc+1]), VecMath.getZ(moonvec[longc][latc+1]), Spmath.fmod(longd+0.5,1.0), latdd);
                    tessellator1.draw();
            	}
            }
        	
            
           /* tessellator1.startDrawingQuads();
            tessellator1.addVertexWithUV((double)(-f10), -100.0D, (double)1.0, (double)f16, (double)f17);
            tessellator1.addVertexWithUV((double)f10, -100.0D, (double)f10, (double)f14, (double)f17);
            tessellator1.addVertexWithUV((double)f10, -100.0D, (double)(-f10), (double)f14, (double)f15);
            tessellator1.addVertexWithUV((double)(-f10), -100.0D, (double)(-f10), (double)f16, (double)f15);
            tessellator1.draw();*/
            //Moon
            
            renderEngine.bindTexture(locationStarPng);
            this.DrawStellarObj(f1+f2+f3, f4, StellarManager.Mercury.AppPos,StellarManager.Mercury.App_Mag);
            this.DrawStellarObj(f1+f2+f3, f4, StellarManager.Venus.AppPos,StellarManager.Venus.App_Mag);
            this.DrawStellarObj(f1+f2+f3, f4, StellarManager.Mars.AppPos,StellarManager.Mars.App_Mag);
            this.DrawStellarObj(f1+f2+f3, f4, StellarManager.Jupiter.AppPos,StellarManager.Jupiter.App_Mag);
            this.DrawStellarObj(f1+f2+f3, f4, StellarManager.Saturn.AppPos,StellarManager.Saturn.App_Mag);
            this.DrawStellarObj(f1+f2+f3, f4, StellarManager.Uranus.AppPos,StellarManager.Uranus.App_Mag);
            this.DrawStellarObj(f1+f2+f3, f4, StellarManager.Neptune.AppPos,StellarManager.Neptune.App_Mag);
            
            
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            float f18 = world.getStarBrightness(par1) * f4;


            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_FOG);
            GL11.glPopMatrix();
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glColor3f(0.0F, 0.0F, 0.0F);
            double d0 = mc.thePlayer.getPosition(par1).yCoord - world.getHorizon();

            if (d0 < 0.0D)
            {
                f8 = 1.0F;
                f9 = -((float)(d0 + 65.0D));
                f10 = -f8;
                tessellator1.startDrawingQuads();
                tessellator1.setColorRGBA_I(0, 255);
                tessellator1.addVertex((double)(-f8), (double)f9, (double)f8);
                tessellator1.addVertex((double)f8, (double)f9, (double)f8);
                tessellator1.addVertex((double)f8, (double)f10, (double)f8);
                tessellator1.addVertex((double)(-f8), (double)f10, (double)f8);
                tessellator1.addVertex((double)(-f8), (double)f10, (double)(-f8));
                tessellator1.addVertex((double)f8, (double)f10, (double)(-f8));
                tessellator1.addVertex((double)f8, (double)f9, (double)(-f8));
                tessellator1.addVertex((double)(-f8), (double)f9, (double)(-f8));
                tessellator1.addVertex((double)f8, (double)f10, (double)(-f8));
                tessellator1.addVertex((double)f8, (double)f10, (double)f8);
                tessellator1.addVertex((double)f8, (double)f9, (double)f8);
                tessellator1.addVertex((double)f8, (double)f9, (double)(-f8));
                tessellator1.addVertex((double)(-f8), (double)f9, (double)(-f8));
                tessellator1.addVertex((double)(-f8), (double)f9, (double)f8);
                tessellator1.addVertex((double)(-f8), (double)f10, (double)f8);
                tessellator1.addVertex((double)(-f8), (double)f10, (double)(-f8));
                tessellator1.addVertex((double)(-f8), (double)f10, (double)(-f8));
                tessellator1.addVertex((double)(-f8), (double)f10, (double)f8);
                tessellator1.addVertex((double)f8, (double)f10, (double)f8);
                tessellator1.addVertex((double)f8, (double)f10, (double)(-f8));
                tessellator1.draw();
            }

            if (world.provider.isSkyColored())
            {
                GL11.glColor3f(f1 * 0.2F + 0.04F, f2 * 0.2F + 0.04F, f3 * 0.6F + 0.1F);
            }
            else
            {
                GL11.glColor3f(f1, f2, f3);
            }

            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDepthMask(true);
        }
		
	}
	
	EVector dif = new EVector(3);
	EVector dif2 = new EVector(3);
	
	public void RenderStar(float bglight, float weathereff, double time){

		GL11.glEnable(GL11.GL_COLOR_MATERIAL);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		
		renderEngine.bindTexture(locationStarPng);
		
		EVector pos = new EVector(3);
           
        if(!world.provider.isHellWorld){
        for(int i=0; i<BrStar.NumStar; i++){
        	if(BrStar.stars[i].unable) continue;
//        	GL11.((7-Star.stars[i].Mag)*30.0f,(7-Star.stars[i].Mag)*30.0f,(7-Star.stars[i].Mag)*30.0f);
        	
        	BrStar star=BrStar.stars[i];
        	
        	
        	pos.set(VecMath.normalize(star.AppPos));
        	float Mag=star.App_Mag;
        	float B_V=star.App_B_V;
        	
        	if(Mag > StellarManager.Mag_Limit)
        		continue;
        	
        	float Turb = StellarManager.Turb *(float) random.nextGaussian();
        	Mag+=Turb;
        	
        	if(VecMath.getZ(pos)<0) continue;
        	
        	float size=0.5f;
        	float alpha=Optics.GetAlphaFromMagnitude(Mag, bglight);
        	
        	dif.set(CrossUtil.cross(pos, new EVector(0.0,0.0,1.0)));
        	if(Spmath.getD(VecMath.size2(dif)) < 0.01)
        		dif.set(CrossUtil.cross(pos, new EVector(0.0,1.0,0.0)));
        	dif.set(VecMath.normalize(dif));
        	dif2.set((IValRef)CrossUtil.cross(dif, pos));
        	pos.set(VecMath.mult(100.0, pos));
        	
        	dif.set(VecMath.mult(size, dif));
        	dif2.set(VecMath.mult(size, dif2));
        	
        	Color c=Color.GetColor(B_V);
   

        	GL11.glColor4f(((float)c.r)/255.0f, ((float)c.g)/255.0f, ((float)c.b)/255.0f, weathereff*alpha);
        	
            tessellator1.startDrawingQuads();

        	tessellator1.addVertexWithUV(VecMath.getX(pos)+VecMath.getX(dif), VecMath.getY(pos)+VecMath.getY(dif), VecMath.getZ(pos)+VecMath.getZ(dif),0.0,0.0);
        	tessellator1.addVertexWithUV(VecMath.getX(pos)+VecMath.getX(dif2), VecMath.getY(pos)+VecMath.getY(dif2), VecMath.getZ(pos)+VecMath.getZ(dif2),1.0,0.0);
        	tessellator1.addVertexWithUV(VecMath.getX(pos)-VecMath.getX(dif), VecMath.getY(pos)-VecMath.getY(dif), VecMath.getZ(pos)-VecMath.getZ(dif),1.0,1.0);
        	tessellator1.addVertexWithUV(VecMath.getX(pos)-VecMath.getX(dif2), VecMath.getY(pos)-VecMath.getY(dif2), VecMath.getZ(pos)-VecMath.getZ(dif2),0.0,1.0);
        	
            tessellator1.draw();
                                  
        }
        
        }

	}
	
	EVector difm = new EVector(3);
	EVector difm2 = new EVector(3);

	public void DrawStellarObj(float bglight, float weathereff, EVector pos, double Mag){
		
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		
		if(Mag > StellarManager.Mag_Limit) return;
		if(VecMath.getZ(pos)<0) return;
		
		float size=0.6f;
    	float alpha=Optics.GetAlphaFromMagnitude(Mag, bglight);
		
		pos.set(VecMath.normalize(pos));
		
    	difm.set(CrossUtil.cross((IEVector)pos, (IEVector)new EVector(0.0,0.0,1.0)));
    	if(Spmath.getD(VecMath.size2(difm)) < 0.01)
    		difm.set(CrossUtil.cross((IEVector)pos, (IEVector)new EVector(0.0,1.0,0.0)));
    	difm.set(VecMath.normalize(difm));
    	difm2.set((IValRef)CrossUtil.cross((IEVector)difm, (IEVector)pos));
    	pos.set(VecMath.mult(99.0, pos));
    	
    	difm.set(VecMath.mult(size, difm));
    	difm2.set(VecMath.mult(size, difm2));
    
    	GL11.glColor4d(1.0, 1.0, 1.0, weathereff*alpha);
        
        tessellator1.startDrawingQuads();
    	tessellator1.addVertexWithUV(VecMath.getX(pos)+VecMath.getX(difm), VecMath.getY(pos)+VecMath.getY(difm), VecMath.getZ(pos)+VecMath.getZ(difm),0.0,0.0);
    	tessellator1.addVertexWithUV(VecMath.getX(pos)+VecMath.getX(difm2), VecMath.getY(pos)+VecMath.getY(difm2), VecMath.getZ(pos)+VecMath.getZ(difm2),1.0,0.0);
    	tessellator1.addVertexWithUV(VecMath.getX(pos)-VecMath.getX(difm), VecMath.getY(pos)-VecMath.getY(difm), VecMath.getZ(pos)-VecMath.getZ(difm),1.0,1.0);
    	tessellator1.addVertexWithUV(VecMath.getX(pos)-VecMath.getX(difm2), VecMath.getY(pos)-VecMath.getY(difm2), VecMath.getZ(pos)-VecMath.getZ(difm2),0.0,1.0);

        tessellator1.draw();
	}
}
