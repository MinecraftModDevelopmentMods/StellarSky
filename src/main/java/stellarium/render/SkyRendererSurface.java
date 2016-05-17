package stellarium.render;


import java.util.Random;

import org.lwjgl.opengl.GL11;

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
import stellarium.api.ICelestialRenderer;

public class SkyRendererSurface extends IRenderHandler {

	private Random random;
	
    private int skyList;
    private int skyListUnderPlayer;
    
    private ICelestialRenderer celestials;

	public SkyRendererSurface(ICelestialRenderer subRenderer) {
        Tessellator tessellator = Tessellator.instance;
        this.skyList = GLAllocation.generateDisplayLists(2);
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
        this.skyListUnderPlayer = this.skyList + 1;
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
        GL11.glEndList();
        
        this.celestials = subRenderer;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void render(float partialTicks, WorldClient theWorld, Minecraft mc) {
        if (theWorld.provider.isSurfaceWorld())
        {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            Vec3 vec3 = theWorld.getSkyColor(mc.renderViewEntity, partialTicks);
            float f1 = (float)vec3.xCoord;
            float f2 = (float)vec3.yCoord;
            float f3 = (float)vec3.zCoord;
            float f6;

            if (mc.gameSettings.anaglyph)
            {
                float f4 = (f1 * 30.0F + f2 * 59.0F + f3 * 11.0F) / 100.0F;
                float f5 = (f1 * 30.0F + f2 * 70.0F) / 100.0F;
                f6 = (f1 * 30.0F + f3 * 70.0F) / 100.0F;
                f1 = f4;
                f2 = f5;
                f3 = f6;
            }

            GL11.glColor3f(f1, f2, f3);
            Tessellator tessellator1 = Tessellator.instance;
            GL11.glDepthMask(false);
            GL11.glEnable(GL11.GL_FOG);
            GL11.glColor3f(f1, f2, f3);
            GL11.glCallList(this.skyList);
            GL11.glDisable(GL11.GL_FOG);
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
            
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            OpenGlHelper.glBlendFunc(770, 1, 1, 0);
            GL11.glPushMatrix();
            GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F); // e,n,z
            celestials.renderCelestial(mc, theWorld, new float[] {f1, f2, f3}, partialTicks);
            GL11.glPopMatrix();
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_FOG);
            GL11.glColor3f(0.0F, 0.0F, 0.0F);
            double d0 = mc.thePlayer.getPosition(partialTicks).yCoord - theWorld.getHorizon();

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

            GL11.glPushMatrix();
            GL11.glTranslatef(0.0F, -((float)(d0 - 16.0D)), 0.0F);
            GL11.glCallList(this.skyListUnderPlayer);
            GL11.glPopMatrix();
            
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            
            GL11.glPushMatrix();
            GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F); // e,n,z
    		GL11.glEnable(GL11.GL_FOG);
            celestials.renderSkyLandscape(mc, theWorld, partialTicks);
            GL11.glDisable(GL11.GL_FOG);
            GL11.glPopMatrix();
            
            GL11.glDepthMask(true);
        }
	}
}
