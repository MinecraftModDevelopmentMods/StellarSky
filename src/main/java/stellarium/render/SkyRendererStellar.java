package stellarium.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
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

public class SkyRendererStellar extends SkyRendererVanilla {

	private static final ResourceLocation locationEndSkyPng = new ResourceLocation("textures/environment/end_sky.png");
    private static final ResourceLocation locationSunPng = new ResourceLocation("stellarium", "stellar/halo.png");
    private static final ResourceLocation locationMoonPng = new ResourceLocation("stellarium", "stellar/lune.png");
    private static final ResourceLocation locationStarPng = new ResourceLocation("stellarium", "stellar/star.png");
    private static final ResourceLocation locationhalolunePng = new ResourceLocation("stellarium", "stellar/haloLune.png");
    
    private EVector dif = new EVector(3);
    private EVector dif2 = new EVector(3);
    private EVector difm = new EVector(3);
    private EVector difm2 = new EVector(3);
    
    protected void renderStellarRaw(int pass, float partialTicks, Tessellator tessellator, WorldRenderer worldRenderer, float red, float green, float blue) {
    	GL11.glPushMatrix();
        float weatherEffect = 1.0F - theWorld.getRainStrength(partialTicks);
        float bglight=red+blue+green;
        
       
        GL11.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f); //e,n,z
        
        GL11.glColor4f(1.0F, 1.0F, 1.0F, weatherEffect);
        
        
        double time = (double)theWorld.getWorldTime() + partialTicks;
                   
        
        //this.RenderStar(bglight, weatherEffect, time);
        
        
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        
        GL11.glColor4f(1.0F, 1.0F, 1.0F, weatherEffect);

        
        //Rendering Sun

        EVector pos = new EVector(3);
        pos.set(StellarSky.getManager().Sun.GetPosition());
        double size=StellarSky.getManager().Sun.Radius/Spmath.getD(VecMath.size(pos))*99.0*20;
        pos.set(VecMath.normalize(pos));
    	dif.set(VOp.normalize(CrossUtil.cross((IEVector)pos, (IEVector)new EVector(0.0,0.0,1.0))));
    	dif2.set((IValRef)CrossUtil.cross((IEVector)dif, (IEVector)pos));
    	pos.set(VecMath.mult(99.0, pos));
   
    	dif.set(VecMath.mult(size, dif));
    	dif2.set(VecMath.mult(size, dif2));
    	
        renderEngine.bindTexture(this.locationSunPng);
        worldRenderer.startDrawingQuads();
        worldRenderer.addVertexWithUV(VecMath.getX(pos)+VecMath.getX(dif), VecMath.getY(pos)+VecMath.getY(dif), VecMath.getZ(pos)+VecMath.getZ(dif),0.0,0.0);
        worldRenderer.addVertexWithUV(VecMath.getX(pos)+VecMath.getX(dif2), VecMath.getY(pos)+VecMath.getY(dif2), VecMath.getZ(pos)+VecMath.getZ(dif2),1.0,0.0);
        worldRenderer.addVertexWithUV(VecMath.getX(pos)-VecMath.getX(dif), VecMath.getY(pos)-VecMath.getY(dif), VecMath.getZ(pos)-VecMath.getZ(dif),1.0,1.0);
        worldRenderer.addVertexWithUV(VecMath.getX(pos)-VecMath.getX(dif2), VecMath.getY(pos)-VecMath.getY(dif2), VecMath.getZ(pos)-VecMath.getZ(dif2),0.0,1.0);
        tessellator.draw();
        //Sun
      
        
        //Rendering Moon
        
        int latn=StellarSky.getManager().ImgFrac, longn=2*StellarSky.getManager().ImgFrac;
        EVector moonvec[][];
        double moonilum[][];
        moonvec=new EVector[longn][latn+1];
        moonilum=new double[longn][latn+1];
        EVector Buf = new EVector(3);
        EVector Buff = new EVector(3);
        int latc, longc;
        for(longc=0; longc<longn; longc++){
        	for(latc=0; latc<=latn; latc++){
        		Buf.set(StellarSky.getManager().Moon.PosLocalM((double)longc/(double)longn*360.0, (double)latc/(double)latn*180.0-90.0, Transforms.yr));
        		moonilum[longc][latc]=StellarSky.getManager().Moon.Illumination(Buf);
        		Buf.set(StellarSky.getManager().Moon.PosLocalG(Buf));
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
      
        
        
               
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        
        renderEngine.bindTexture(locationhalolunePng);
        
        EVector posm = new EVector(3);
        
        
        posm.set(ExtinctionRefraction.Refraction(StellarSky.getManager().Moon.GetPosition(), true));
        
        if(VecMath.getZ(posm)>0.0f){
        double sizem=StellarSky.getManager().Moon.Radius.asDouble()/Spmath.getD(VecMath.size(posm))*98.0*5.0;
       
        posm.set(VOp.normalize(posm));
    	difm.set(VOp.normalize(CrossUtil.cross((IEVector)posm, (IEVector)new EVector(0.0,0.0,1.0))));
    	difm2.set((IValRef)CrossUtil.cross((IEVector)difm, (IEVector)posm));
    	posm.set(VecMath.mult(98.0, posm));
    	
    	difm.set(VecMath.mult(sizem, difm));
    	difm2.set(VecMath.mult(sizem, difm2));
    	
    	float alpha=Optics.GetAlphaFromMagnitude(-17.0-StellarSky.getManager().Moon.Mag,bglight);
    	
        GL11.glColor4d(1.0, 1.0, 1.0, weatherEffect*alpha);
        
        worldRenderer.startDrawingQuads();
        worldRenderer.addVertexWithUV(VecMath.getX(posm)+VecMath.getX(difm), VecMath.getY(posm)+VecMath.getY(difm), VecMath.getZ(posm)+VecMath.getZ(difm),0.0,0.0);
        worldRenderer.addVertexWithUV(VecMath.getX(posm)+VecMath.getX(difm2), VecMath.getY(posm)+VecMath.getY(difm2), VecMath.getZ(posm)+VecMath.getZ(difm2),0.0,1.0);
        worldRenderer.addVertexWithUV(VecMath.getX(posm)-VecMath.getX(difm), VecMath.getY(posm)-VecMath.getY(difm), VecMath.getZ(posm)-VecMath.getZ(difm),1.0,1.0);
        worldRenderer.addVertexWithUV(VecMath.getX(posm)-VecMath.getX(difm2), VecMath.getY(posm)-VecMath.getY(difm2), VecMath.getZ(posm)-VecMath.getZ(difm2),1.0,0.0);
    	tessellator.draw();
        }
    	
    	
        renderEngine.bindTexture(locationMoonPng);
        
        
        for(longc=0; longc<longn; longc++){
        	for(latc=0; latc<latn; latc++){
        		
        		int longcd=(longc+1)%longn;
        		double longd=(double)longc/(double)longn;
        		double latd=1.0-(double)latc/(double)latn;
        		double longdd=(double)longcd/(double)longn;
        		double latdd=1.0-(double)(latc+1)/(double)latn;
        		
                GL11.glColor4d(1.0, 1.0, 1.0, (weatherEffect*moonilum[longc][latc]-4.0f*bglight)*2.0f);
            	
                worldRenderer.startDrawingQuads();
                worldRenderer.addVertexWithUV(VecMath.getX(moonvec[longc][latc]), VecMath.getY(moonvec[longc][latc]), VecMath.getZ(moonvec[longc][latc]), Spmath.fmod(longd+0.5, 1.0), latd);
                worldRenderer.addVertexWithUV(VecMath.getX(moonvec[longcd][latc]), VecMath.getY(moonvec[longcd][latc]), VecMath.getZ(moonvec[longcd][latc]), Spmath.fmod(longdd+0.5,1.0), latd);
                worldRenderer.addVertexWithUV(VecMath.getX(moonvec[longcd][latc+1]), VecMath.getY(moonvec[longcd][latc+1]), VecMath.getZ(moonvec[longcd][latc+1]), Spmath.fmod(longdd+0.5, 1.0), latdd);
                worldRenderer.addVertexWithUV(VecMath.getX(moonvec[longc][latc+1]), VecMath.getY(moonvec[longc][latc+1]), VecMath.getZ(moonvec[longc][latc+1]), Spmath.fmod(longd+0.5,1.0), latdd);
                tessellator.draw();
        	}
        }
        //Moon
    }
    
    protected void renderStellar(int pass, float partialTicks, Tessellator tessellator, WorldRenderer worldrenderer) {
    	GlStateManager.enableBlend();
    	GlStateManager.disableFog();
    	GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
        GlStateManager.depthMask(false);
        
        GlStateManager.pushMatrix();
        float weatherEffect = 1.0F - theWorld.getRainStrength(partialTicks);
        GlStateManager.color(1.0F, 1.0F, 1.0F, weatherEffect);
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(theWorld.getCelestialAngle(partialTicks) * 360.0F, 1.0F, 0.0F, 0.0F);
        float stellarDepth = 30.0F;
        this.renderEngine.bindTexture(locationSunPng);
        worldrenderer.startDrawingQuads();
        worldrenderer.addVertexWithUV((double)(-stellarDepth), 100.0D, (double)(-stellarDepth), 0.0D, 0.0D);
        worldrenderer.addVertexWithUV((double)stellarDepth, 100.0D, (double)(-stellarDepth), 1.0D, 0.0D);
        worldrenderer.addVertexWithUV((double)stellarDepth, 100.0D, (double)stellarDepth, 1.0D, 1.0D);
        worldrenderer.addVertexWithUV((double)(-stellarDepth), 100.0D, (double)stellarDepth, 0.0D, 1.0D);
        tessellator.draw();
        stellarDepth = 20.0F;
        this.renderEngine.bindTexture(locationMoonPng);
        int k = theWorld.getMoonPhase();
        int l = k % 4;
        int i1 = k / 4 % 2;
        float red5 = (float)(l + 0) / 4.0F;
        float red6 = (float)(i1 + 0) / 2.0F;
        float red7 = (float)(l + 1) / 4.0F;
        float red8 = (float)(i1 + 1) / 2.0F;
        worldrenderer.startDrawingQuads();
        worldrenderer.addVertexWithUV((double)(-stellarDepth), -100.0D, (double)stellarDepth, (double)red7, (double)red8);
        worldrenderer.addVertexWithUV((double)stellarDepth, -100.0D, (double)stellarDepth, (double)red5, (double)red8);
        worldrenderer.addVertexWithUV((double)stellarDepth, -100.0D, (double)(-stellarDepth), (double)red5, (double)red6);
        worldrenderer.addVertexWithUV((double)(-stellarDepth), -100.0D, (double)(-stellarDepth), (double)red7, (double)red6);
        tessellator.draw();
        
        GlStateManager.disableTexture2D();
    	GlStateManager.disableAlpha();
        float brstar = theWorld.getStarBrightness(partialTicks) * weatherEffect;
        if (brstar > 0.0F)
        {
            GlStateManager.color(brstar, brstar, brstar, brstar);

            if (this.vboEnabled)
            {
                this.starVBO.bindBuffer();
                GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
                GL11.glVertexPointer(3, GL11.GL_FLOAT, 12, 0L);
                this.starVBO.drawArrays(7);
                this.starVBO.unbindBuffer();
                GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
            }
            else
            {
                GlStateManager.callList(this.starGLCallList);
            }
        }
        GlStateManager.enableTexture2D();
        GlStateManager.enableAlpha();

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.enableFog();
        GlStateManager.popMatrix();
        GlStateManager.depthMask(true);
    }	
}
