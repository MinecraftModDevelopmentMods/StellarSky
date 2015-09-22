package stellarium.render;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.IRenderHandler;

public class SkyRendererVanilla extends IRenderHandler {
	
	protected WorldClient theWorld;
	protected Minecraft mc;
	protected TextureManager renderEngine;
	
    protected int glSkyList = -1;
    protected int glSkyList2 = -1;
    protected int starGLCallList = -1;
    
    protected VertexFormat vertexBufferFormat;
	protected boolean vboEnabled;
    protected VertexBuffer starVBO;
    protected VertexBuffer skyVBO;
    protected VertexBuffer sky2VBO;
	
    protected static final ResourceLocation locationMoonPng = new ResourceLocation("textures/environment/moon_phases.png");
    protected static final ResourceLocation locationSunPng = new ResourceLocation("textures/environment/sun.png");
    protected static final ResourceLocation locationEndSkyPng = new ResourceLocation("textures/environment/end_sky.png");
    
    public SkyRendererVanilla() {
    	this.vertexBufferFormat = new VertexFormat();
        this.vertexBufferFormat.setElement(new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUsage.POSITION, 3));
    	
        this.setup();
    }
    
    protected void setup() {
    	if(this.theWorld == null)
    		return;
    	
    	boolean flag = this.vboEnabled;
    	this.vboEnabled = OpenGlHelper.useVbo();
    	
    	if(flag != this.vboEnabled) {
    		this.generateSky();
    		this.generateSky2();
    		this.generateStars();
    	}
    }
    
	@Override
	public void render(float partialTicks, WorldClient world, Minecraft mc) {
		this.mc = mc;
		this.theWorld = world;
		this.renderEngine = mc.getTextureManager();
		int pass = ForgeHooksClient.getWorldRenderPass();
		
		this.setup();
		this.renderSky(pass, partialTicks);
	}
	
	
    protected void generateSky()
    {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        if (this.skyVBO != null)
        {
            this.skyVBO.deleteGlBuffers();
        }

        if (this.glSkyList >= 0)
        {
            GLAllocation.deleteDisplayLists(this.glSkyList);
            this.glSkyList = -1;
        }

        if (this.vboEnabled)
        {
            this.skyVBO = new VertexBuffer(this.vertexBufferFormat);
            this.renderSkyInternal(worldrenderer, 16.0F, false);
            worldrenderer.finishDrawing();
            worldrenderer.reset();
            this.skyVBO.bufferData(worldrenderer.getByteBuffer(), worldrenderer.getByteIndex());
        }
        else
        {
            this.glSkyList = GLAllocation.generateDisplayLists(1);
            GL11.glNewList(this.glSkyList, GL11.GL_COMPILE);
            this.renderSkyInternal(worldrenderer, 16.0F, false);
            tessellator.draw();
            GL11.glEndList();
        }
    }
    
    protected void generateSky2()
    {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        if (this.sky2VBO != null)
        {
            this.sky2VBO.deleteGlBuffers();
        }

        if (this.glSkyList2 >= 0)
        {
            GLAllocation.deleteDisplayLists(this.glSkyList2);
            this.glSkyList2 = -1;
        }

        if (this.vboEnabled)
        {
            this.sky2VBO = new VertexBuffer(this.vertexBufferFormat);
            this.renderSkyInternal(worldrenderer, -16.0F, true);
            worldrenderer.finishDrawing();
            worldrenderer.reset();
            this.sky2VBO.bufferData(worldrenderer.getByteBuffer(), worldrenderer.getByteIndex());
        }
        else
        {
            this.glSkyList2 = GLAllocation.generateDisplayLists(1);
            GL11.glNewList(this.glSkyList2, GL11.GL_COMPILE);
            this.renderSkyInternal(worldrenderer, -16.0F, true);
            tessellator.draw();
            GL11.glEndList();
        }
    }
    
    protected void generateStars()
    {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        if (this.starVBO != null)
        {
            this.starVBO.deleteGlBuffers();
        }

        if (this.starGLCallList >= 0)
        {
            GLAllocation.deleteDisplayLists(this.starGLCallList);
            this.starGLCallList = -1;
        }

        if (this.vboEnabled)
        {
            this.starVBO = new VertexBuffer(this.vertexBufferFormat);
            this.renderStarsInternal(worldrenderer);
            worldrenderer.finishDrawing();
            worldrenderer.reset();
            this.starVBO.bufferData(worldrenderer.getByteBuffer(), worldrenderer.getByteIndex());
        }
        else
        {
            this.starGLCallList = GLAllocation.generateDisplayLists(1);
            GlStateManager.pushMatrix();
            GL11.glNewList(this.starGLCallList, GL11.GL_COMPILE);
            this.renderStarsInternal(worldrenderer);
            tessellator.draw();
            GL11.glEndList();
            GlStateManager.popMatrix();
        }
    }
    
    protected void renderSkyInternal(WorldRenderer worldRendererIn, float p_174968_2_, boolean p_174968_3_)
    {
        worldRendererIn.startDrawingQuads();

        for (int i = -384; i <= 384; i += 64)
        {
            for (int j = -384; j <= 384; j += 64)
            {
                float red = (float)i;
                float green = (float)(i + 64);

                if (p_174968_3_)
                {
                    green = (float)i;
                    red = (float)(i + 64);
                }

                worldRendererIn.addVertex((double)red, (double)p_174968_2_, (double)j);
                worldRendererIn.addVertex((double)green, (double)p_174968_2_, (double)j);
                worldRendererIn.addVertex((double)green, (double)p_174968_2_, (double)(j + 64));
                worldRendererIn.addVertex((double)red, (double)p_174968_2_, (double)(j + 64));
            }
        }
    }
    
    protected void renderStarsInternal(WorldRenderer worldRendererIn)
    {
        Random random = new Random(10842L);
        worldRendererIn.startDrawingQuads();

        for (int i = 0; i < 1500; ++i)
        {
            double d0 = (double)(random.nextFloat() * 2.0F - 1.0F);
            double d1 = (double)(random.nextFloat() * 2.0F - 1.0F);
            double d2 = (double)(random.nextFloat() * 2.0F - 1.0F);
            double d3 = (double)(0.15F + random.nextFloat() * 0.1F);
            double d4 = d0 * d0 + d1 * d1 + d2 * d2;

            if (d4 < 1.0D && d4 > 0.01D)
            {
                d4 = 1.0D / Math.sqrt(d4);
                d0 *= d4;
                d1 *= d4;
                d2 *= d4;
                double d5 = d0 * 100.0D;
                double d6 = d1 * 100.0D;
                double d7 = d2 * 100.0D;
                double d8 = Math.atan2(d0, d2);
                double d9 = Math.sin(d8);
                double d10 = Math.cos(d8);
                double d11 = Math.atan2(Math.sqrt(d0 * d0 + d2 * d2), d1);
                double d12 = Math.sin(d11);
                double d13 = Math.cos(d11);
                double d14 = random.nextDouble() * Math.PI * 2.0D;
                double d15 = Math.sin(d14);
                double d16 = Math.cos(d14);

                for (int j = 0; j < 4; ++j)
                {
                    double d17 = 0.0D;
                    double d18 = (double)((j & 2) - 1) * d3;
                    double d19 = (double)((j + 1 & 2) - 1) * d3;
                    double d20 = 0.0D;
                    double d21 = d18 * d16 - d19 * d15;
                    double d22 = d19 * d16 + d18 * d15;
                    double d23 = d21 * d12 + 0.0D * d13;
                    double d24 = 0.0D * d12 - d21 * d13;
                    double d25 = d24 * d9 - d22 * d10;
                    double d26 = d22 * d9 + d24 * d10;
                    worldRendererIn.addVertex(d5 + d25, d6 + d23, d7 + d26);
                }
            }
        }
    }
    
    protected void renderSkyBg(int pass, float partialTicks, Tessellator tessellator, WorldRenderer worldrenderer, float red, float green, float blue) {
        GlStateManager.disableTexture2D();
    	GlStateManager.color(red, green, blue);
        GlStateManager.depthMask(false);
        GlStateManager.enableFog();
        GlStateManager.color(red, green, blue);

        if (this.vboEnabled)
        {
            this.skyVBO.bindBuffer();
            GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
            GL11.glVertexPointer(3, GL11.GL_FLOAT, 12, 0L);
            this.skyVBO.drawArrays(7);
            this.skyVBO.unbindBuffer();
            GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
        }
        else
        {
            GlStateManager.callList(this.glSkyList);
        }
        
        GlStateManager.disableFog();
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
    }
    
    protected void renderSkyGlow(int pass, float partialTicks, Tessellator tessellator, WorldRenderer worldrenderer) {
    	GlStateManager.disableAlpha();
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        RenderHelper.disableStandardItemLighting();
        float[] afloat = theWorld.provider.calcSunriseSunsetColors(theWorld.getCelestialAngle(partialTicks), partialTicks);
        float glowRed;
        float glowGreen;
        float glowBlue;

        if (afloat != null)
        {
            GlStateManager.disableTexture2D();
            GlStateManager.shadeModel(7425);
            GlStateManager.pushMatrix();
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(MathHelper.sin(theWorld.getCelestialAngleRadians(partialTicks)) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
            glowRed = afloat[0];
            glowGreen = afloat[1];
            glowBlue = afloat[2];

            if (pass != 2)
            {
                float bufferRed = (glowRed * 30.0F + glowGreen * 59.0F + glowBlue * 11.0F) / 100.0F;
                float bufferGreen = (glowRed * 30.0F + glowGreen * 70.0F) / 100.0F;
                float bufferBlue = (glowRed * 30.0F + glowBlue * 70.0F) / 100.0F;
                glowRed = bufferRed;
                glowGreen = bufferGreen;
                glowBlue = bufferBlue;
            }

            worldrenderer.startDrawing(6);
            worldrenderer.setColorRGBA_F(glowRed, glowGreen, glowBlue, afloat[3]);
            worldrenderer.addVertex(0.0D, 100.0D, 0.0D);
            boolean flag = true;
            worldrenderer.setColorRGBA_F(afloat[0], afloat[1], afloat[2], 0.0F);
            
            for (int j = 0; j <= 16; ++j)
            {
                float angleFactor = (float)j * (float)Math.PI * 2.0F / 16.0F;
                float sinFactor = MathHelper.sin(angleFactor);
                float cosFactor = MathHelper.cos(angleFactor);
                worldrenderer.addVertex((double)(sinFactor * 120.0F), (double)(cosFactor * 120.0F), (double)(-cosFactor * 40.0F * afloat[3]));
            }

            tessellator.draw();
            GlStateManager.popMatrix();
            GlStateManager.shadeModel(7424);
            GlStateManager.enableTexture2D();
        }
        
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
    	GlStateManager.enableAlpha();
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
        float red9 = theWorld.getStarBrightness(partialTicks) * weatherEffect;
        if (red9 > 0.0F)
        {
            GlStateManager.color(red9, red9, red9, red9);

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

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableFog();
        GlStateManager.popMatrix();
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(true);
    }
    
    protected void renderSkyOverlay(int pass, float partialTicks, Tessellator tessellator, WorldRenderer worldrenderer, float red, float green, float blue) {
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
    	GlStateManager.color(0.0F, 0.0F, 0.0F);
        double d0 = mc.thePlayer.getPositionEyes(partialTicks).yCoord - theWorld.getHorizon();

        if (d0 < 0.0D)
        {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, 12.0F, 0.0F);

            if (this.vboEnabled)
            {
                this.sky2VBO.bindBuffer();
                GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
                GL11.glVertexPointer(3, GL11.GL_FLOAT, 12, 0L);
                this.sky2VBO.drawArrays(7);
                this.sky2VBO.unbindBuffer();
                GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
            }
            else
            {
                GlStateManager.callList(this.glSkyList2);
            }

            GlStateManager.popMatrix();
            float overlayDepth = -((float)(d0 + 65.0D));
            worldrenderer.startDrawingQuads();
            worldrenderer.setColorRGBA_I(0, 255);
            worldrenderer.addVertex(-1.0D, (double)overlayDepth, 1.0D);
            worldrenderer.addVertex(1.0D, (double)overlayDepth, 1.0D);
            worldrenderer.addVertex(1.0D, -1.0D, 1.0D);
            worldrenderer.addVertex(-1.0D, -1.0D, 1.0D);
            worldrenderer.addVertex(-1.0D, -1.0D, -1.0D);
            worldrenderer.addVertex(1.0D, -1.0D, -1.0D);
            worldrenderer.addVertex(1.0D, (double)overlayDepth, -1.0D);
            worldrenderer.addVertex(-1.0D, (double)overlayDepth, -1.0D);
            worldrenderer.addVertex(1.0D, -1.0D, -1.0D);
            worldrenderer.addVertex(1.0D, -1.0D, 1.0D);
            worldrenderer.addVertex(1.0D, (double)overlayDepth, 1.0D);
            worldrenderer.addVertex(1.0D, (double)overlayDepth, -1.0D);
            worldrenderer.addVertex(-1.0D, (double)overlayDepth, -1.0D);
            worldrenderer.addVertex(-1.0D, (double)overlayDepth, 1.0D);
            worldrenderer.addVertex(-1.0D, -1.0D, 1.0D);
            worldrenderer.addVertex(-1.0D, -1.0D, -1.0D);
            worldrenderer.addVertex(-1.0D, -1.0D, -1.0D);
            worldrenderer.addVertex(-1.0D, -1.0D, 1.0D);
            worldrenderer.addVertex(1.0D, -1.0D, 1.0D);
            worldrenderer.addVertex(1.0D, -1.0D, -1.0D);
            tessellator.draw();
        }

        if (theWorld.provider.isSkyColored())
        {
            GlStateManager.color(red * 0.2F + 0.04F, green * 0.2F + 0.04F, blue * 0.6F + 0.1F);
        }
        else
        {
            GlStateManager.color(red, green, blue);
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, -((float)(d0 - 16.0D)), 0.0F);
        GlStateManager.callList(this.glSkyList2);
        GlStateManager.popMatrix();
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(true);
    }
	
	protected void renderSky(int pass, float partialTicks) {
		if (mc.theWorld.provider.getDimensionId() == 1)
		{
            this.renderSkyEnd();
		}
        else if (mc.theWorld.provider.isSurfaceWorld())
        {
        	Vec3 vec3 = theWorld.getSkyColor(mc.getRenderViewEntity(), partialTicks);
            float red = (float)vec3.xCoord;
            float green = (float)vec3.yCoord;
            float blue = (float)vec3.zCoord;

            if (pass != 2)
            {
                float bufferRed = (red * 30.0F + green * 59.0F + blue * 11.0F) / 100.0F;
                float bufferGreen = (red * 30.0F + green * 70.0F) / 100.0F;
                float bufferBlue = (red * 30.0F + blue * 70.0F) / 100.0F;
                red = bufferRed;
                green = bufferGreen;
                blue = bufferBlue;
            }
            
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldrenderer = tessellator.getWorldRenderer();

            this.renderSkyBg(pass, partialTicks, tessellator, worldrenderer, red, green, blue);
            this.renderSkyGlow(pass, partialTicks, tessellator, worldrenderer);
            this.renderStellar(pass, partialTicks, tessellator, worldrenderer);
            this.renderSkyOverlay(pass, partialTicks, tessellator, worldrenderer, red, green, blue);
        }
	}
	
	protected void renderSkyEnd() {
        GlStateManager.disableFog();
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.depthMask(false);
        this.renderEngine.bindTexture(locationEndSkyPng);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        for (int i = 0; i < 6; ++i)
        {
            GlStateManager.pushMatrix();

            if (i == 1)
            {
                GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            }

            if (i == 2)
            {
                GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
            }

            if (i == 3)
            {
                GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
            }

            if (i == 4)
            {
                GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
            }

            if (i == 5)
            {
                GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
            }

            worldrenderer.startDrawingQuads();
            worldrenderer.setColorOpaque_I(2631720);
            worldrenderer.addVertexWithUV(-100.0D, -100.0D, -100.0D, 0.0D, 0.0D);
            worldrenderer.addVertexWithUV(-100.0D, -100.0D, 100.0D, 0.0D, 16.0D);
            worldrenderer.addVertexWithUV(100.0D, -100.0D, 100.0D, 16.0D, 16.0D);
            worldrenderer.addVertexWithUV(100.0D, -100.0D, -100.0D, 16.0D, 0.0D);
            tessellator.draw();
            GlStateManager.popMatrix();
        }

        GlStateManager.depthMask(true);
        GlStateManager.enableAlpha();
	}

}
