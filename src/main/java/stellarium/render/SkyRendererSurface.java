package stellarium.render;


import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Random;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.IRenderHandler;
import stellarapi.api.StellarAPIReference;
import stellarapi.api.celestials.ICelestialObject;
import stellarapi.api.celestials.IEffectorType;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Vector3;
import stellarapi.api.optics.Wavelength;
import stellarium.api.ICelestialRenderer;
import stellarium.render.celesital.EnumRenderPass;
import stellarium.render.shader.ShaderHelper;
import stellarium.util.math.VectorHelper;

public class SkyRendererSurface extends IRenderHandler {
	
    private int skyList;
    private int skyListUnderPlayer;
    
    private ICelestialRenderer celestials;
    private TheSkyRenderer renderer;

	public SkyRendererSurface(ICelestialRenderer subRenderer) {
        Tessellator tessellator = Tessellator.instance;
        
        /*
         * Generate the sky list above the player.
         * It is generated above the player, as surface.
         * */
        /*this.skyList = GLAllocation.generateDisplayLists(2);
        GL11.glNewList(this.skyList, GL11.GL_COMPILE);
        byte b2 = 64;
        int i = 256 / b2 + 2;
        float f = 16.0F;
        int j;
        int k;

        for (j = -b2 * i; j <= b2 * i; j += b2)
        {
            for (k = -b2 * i; k <= b2 * i; k += b2)
            {
                tessellator.startDrawingQuads();
                tessellator.addVertex((double)(j + 0), (double)f, (double)(k + 0));
                tessellator.addVertex((double)(j + b2), (double)f, (double)(k + 0));
                tessellator.addVertex((double)(j + b2), (double)f, (double)(k + b2));
                tessellator.addVertex((double)(j + 0), (double)f, (double)(k + b2));
                tessellator.draw();
            }
        }
        GL11.glEndList();
        
        /*
         * Generate the sky list under the player.
         * It is generated above the player, as surface.
         * */
        /*this.skyListUnderPlayer = this.skyList + 1;
        GL11.glNewList(this.skyListUnderPlayer, GL11.GL_COMPILE);
        f = -16.0F;
        tessellator.startDrawingQuads();

        for (j = -b2 * i; j <= b2 * i; j += b2)
        {
            for (k = -b2 * i; k <= b2 * i; k += b2)
            {
                tessellator.addVertex((double)(j + b2), (double)f, (double)(k + 0));
                tessellator.addVertex((double)(j + 0), (double)f, (double)(k + 0));
                tessellator.addVertex((double)(j + 0), (double)f, (double)(k + b2));
                tessellator.addVertex((double)(j + b2), (double)f, (double)(k + b2));
            }
        }

        tessellator.draw();
        GL11.glEndList();*/
        this.renderer = new TheSkyRenderer(subRenderer);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void render(float partialTicks, WorldClient theWorld, Minecraft mc) {
		renderer.render(partialTicks, theWorld, mc);
	}
	
	@SideOnly(Side.CLIENT)
	public void renderPrevious(float partialTicks, WorldClient theWorld, Minecraft mc) {
        if (theWorld.provider.isSurfaceWorld()) {
        	/*
        	 * TODO Atmospheric Rendering
        	 * 1. Perform accurate atmosphere rendering (Use shaders if possible)
        	 * 2. Atmosphere effects on Celestial Objects
        	 * 
        	 * Rendering Order should be:
        	 * A. Render Pre-Additional Displays
        	 * B. Apply Atmospherical Shaders
        	 * C. Render Celestial Objects
        	 *  C1. Render Distant Objects - On the Same Plane
        	 *  C2. Render Near Objects
        	 * * Objects with greater influence will be written on atmosphere
        	 * D. Render the Atmosphere
        	 * E. Disapply Atmospherical Shaders
        	 * F. Render Post-Additional Displays
        	 * G. Render Landscape - To hide everything under the ground
        	 * */
        	
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            Vec3 vec3 = theWorld.getSkyColor(mc.renderViewEntity, partialTicks);
            float f1 = (float)vec3.xCoord;
            float f2 = (float)vec3.yCoord;
            float f3 = (float)vec3.zCoord;
            float f6;

            /* 
             * When anaglyph is enabled, sky color is modified to fit the effect.
             */
            if (mc.gameSettings.anaglyph)
            {
                float f4 = (f1 * 30.0F + f2 * 59.0F + f3 * 11.0F) / 100.0F;
                float f5 = (f1 * 30.0F + f2 * 70.0F) / 100.0F;
                f6 = (f1 * 30.0F + f3 * 70.0F) / 100.0F;
                f1 = f4;
                f2 = f5;
                f3 = f6;
            }
            
            float f4, f5;
            float alpha = f1 + f2 + f3;
            if(alpha > 0.0f && alpha < 1.0f) {
            	f4 = f1 / alpha;
            	f5 = f2 / alpha;
            	f6 = f3 / alpha;
            } else {
            	f4 = f1;
            	f5 = f2;
            	f6 = f3;
            }
            
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glPushMatrix();
            GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F); // e,n,z
            celestials.renderCelestial(mc, theWorld, new float[] {0.0f, 0.0f, 0.0f}, partialTicks);
            GL11.glPopMatrix();
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            
    		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            
    		/*
             * Draw the sky above the player.
             * Mixed with the fog to give the semi-realistic view.
             */
            GL11.glColor4f(f4, f5, f6, alpha);
            GL11.glPushMatrix();
            Tessellator tessellator1 = Tessellator.instance;
            GL11.glDepthMask(false);
            GL11.glEnable(GL11.GL_FOG);
            GL11.glCallList(this.skyList);
            GL11.glDisable(GL11.GL_FOG);
            GL11.glPopMatrix();
            /*
             * Draw the sunrise/sunset effect.
             * Mixed with the sky color to give semi-realistic view.
             */
            GL11.glDisable(GL11.GL_ALPHA_TEST);   
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            RenderHelper.disableStandardItemLighting();
            float[] afloat = theWorld.provider.calcSunriseSunsetColors(theWorld.getCelestialAngle(partialTicks), partialTicks);
            float f7;
            float f8;
            float f9;
            float f10;

            if (afloat != null)
            	celestials.renderSunriseSunsetEffect(mc, theWorld, afloat, partialTicks);
            
            
            /* 
             * Will draw the sky list under the player.
             */
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_FOG);
            GL11.glColor3f(0.0F, 0.0F, 0.0F);
            double d0 = mc.thePlayer.getPosition(partialTicks).yCoord - theWorld.getHorizon();

            /* 
             * When the player is under the horizon,
             * Draw the sky list under the player in black color,
             * And draw the rectangle under the y -1.0 to hide the sky under y 0
             */
            if (d0 < 0.0D)
            {
                GL11.glPushMatrix();
                GL11.glTranslatef(0.0F, 12.0F, 0.0F);
                GL11.glCallList(this.skyListUnderPlayer);
                GL11.glPopMatrix();
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

            if (theWorld.provider.isSkyColored())
            {
                GL11.glColor3f(f1 * 0.2F + 0.04F, f2 * 0.2F + 0.04F, f3 * 0.6F + 0.1F);
            }
            else
            {
                GL11.glColor3f(f1, f2, f3);
            }

            /* 
             * Draw the sky list under the player in sky color.
             * This is to hide celestial objects under the horizon.
             */
            GL11.glPushMatrix();
            GL11.glTranslatef(0.0F, -((float)(d0 - 16.0D)), 0.0F);
            GL11.glCallList(this.skyListUnderPlayer);
            GL11.glPopMatrix();
            
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            
            GL11.glPushMatrix();
        	GL11.glTranslatef(0.0F, (float)-d0, 0.0F);
            GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F); // e,n,z
            GL11.glColor3f(1.0f, 1.0f, 1.0f);
            celestials.renderSkyLandscape(mc, theWorld, partialTicks);
            GL11.glPopMatrix();
            
            GL11.glDepthMask(true);
        }
	}
}
