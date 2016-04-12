package stellarium.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.IRenderHandler;
import stellarium.api.ICelestialRenderer;
import stellarium.client.ClientSettings;

public class SkyRenderer extends IRenderHandler {
	
    private static final ResourceLocation locationEndSkyPng = new ResourceLocation("textures/environment/end_sky.png");

	private TextureManager renderEngine;
	private Minecraft mc;
	private Tessellator tessellator1=Tessellator.getInstance();

	private VertexFormat vertexBufferFormat;
	private net.minecraft.client.renderer.vertex.VertexBuffer skyVBO;
	private net.minecraft.client.renderer.vertex.VertexBuffer sky2VBO;
	private boolean vboEnabled = false;
	
	private int glSkyList = -1;
	private int glSkyList2 = -1;
	
	private ICelestialRenderer celestials;
	
	public SkyRenderer(ICelestialRenderer celestials){
		this.mc = Minecraft.getMinecraft();
		this.renderEngine = mc.getTextureManager();
		this.vboEnabled = OpenGlHelper.useVbo();
		
		this.vertexBufferFormat = new VertexFormat();
		this.vertexBufferFormat.addElement(new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUsage.POSITION, 2));
		this.generateSky();
		this.generateSky2();
		
		this.celestials = celestials;
	}

	private void generateSky2()
	{
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();

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
            this.sky2VBO = new net.minecraft.client.renderer.vertex.VertexBuffer(this.vertexBufferFormat);
            this.renderSky(vertexbuffer, -16.0F, true);
            vertexbuffer.finishDrawing();
            vertexbuffer.reset();
            this.sky2VBO.bufferData(vertexbuffer.getByteBuffer());
        }
        else
        {
            this.glSkyList2 = GLAllocation.generateDisplayLists(1);
            GlStateManager.glNewList(this.glSkyList2, 4864);
            this.renderSky(vertexbuffer, -16.0F, true);
            tessellator.draw();
            GlStateManager.glEndList();
        }
	}

	private void generateSky()
	{
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();

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
            this.skyVBO = new net.minecraft.client.renderer.vertex.VertexBuffer(this.vertexBufferFormat);
            this.renderSky(vertexbuffer, 16.0F, false);
            vertexbuffer.finishDrawing();
            vertexbuffer.reset();
            this.skyVBO.bufferData(vertexbuffer.getByteBuffer());
        }
        else
        {
            this.glSkyList = GLAllocation.generateDisplayLists(1);
            GlStateManager.glNewList(this.glSkyList, 4864);
            this.renderSky(vertexbuffer, 16.0F, false);
            tessellator.draw();
            GlStateManager.glEndList();
        }
	}

    private void renderSky(VertexBuffer worldRendererIn, float posY, boolean reverseX)
    {
        int i = 64;
        int j = 6;
        worldRendererIn.begin(7, DefaultVertexFormats.POSITION);

        for (int k = -384; k <= 384; k += 64)
        {
            for (int l = -384; l <= 384; l += 64)
            {
                float f = (float)k;
                float f1 = (float)(k + 64);

                if (reverseX)
                {
                    f1 = (float)k;
                    f = (float)(k + 64);
                }

                worldRendererIn.pos((double)f, (double)posY, (double)l).endVertex();
                worldRendererIn.pos((double)f1, (double)posY, (double)l).endVertex();
                worldRendererIn.pos((double)f1, (double)posY, (double)(l + 64)).endVertex();
                worldRendererIn.pos((double)f, (double)posY, (double)(l + 64)).endVertex();
            }
        }
    }
    
	@Override
	public void render(float partialTicks, WorldClient theWorld, Minecraft mc)
	{
		GlStateManager.disableTexture2D();
		Vec3d vec3d = theWorld.getSkyColor(this.mc.getRenderViewEntity(), partialTicks);
		float f = (float)vec3d.xCoord;
		float f1 = (float)vec3d.yCoord;
		float f2 = (float)vec3d.zCoord;

		if (mc.gameSettings.anaglyph)
		{
			float f3 = (f * 30.0F + f1 * 59.0F + f2 * 11.0F) / 100.0F;
			float f4 = (f * 30.0F + f1 * 70.0F) / 100.0F;
			float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
			f = f3;
			f1 = f4;
			f2 = f5;
		}

		GlStateManager.color(f, f1, f2);
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexbuffer = tessellator.getBuffer();
		GlStateManager.depthMask(false);
		GlStateManager.enableFog();
		GlStateManager.color(f, f1, f2);

		if (this.vboEnabled)
		{
			this.skyVBO.bindBuffer();
			GlStateManager.glEnableClientState(32884);
			GlStateManager.glVertexPointer(3, 5126, 12, 0);
			this.skyVBO.drawArrays(7);
			this.skyVBO.unbindBuffer();
			GlStateManager.glDisableClientState(32884);
		}
		else
		{
			GlStateManager.callList(this.glSkyList);
		}

		GlStateManager.disableFog();
		GlStateManager.disableAlpha();
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		RenderHelper.disableStandardItemLighting();
		float[] afloat = theWorld.provider.calcSunriseSunsetColors(theWorld.getCelestialAngle(partialTicks), partialTicks);

		if (afloat != null)
		{
			GlStateManager.disableTexture2D();
			GlStateManager.shadeModel(7425);
			GlStateManager.pushMatrix();
			GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(MathHelper.sin(theWorld.getCelestialAngleRadians(partialTicks)) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
			float f6 = afloat[0];
			float f7 = afloat[1];
			float f8 = afloat[2];

			if (mc.gameSettings.anaglyph)
			{
				float f9 = (f6 * 30.0F + f7 * 59.0F + f8 * 11.0F) / 100.0F;
				float f10 = (f6 * 30.0F + f7 * 70.0F) / 100.0F;
				float f11 = (f6 * 30.0F + f8 * 70.0F) / 100.0F;
				f6 = f9;
				f7 = f10;
				f8 = f11;
			}

			vertexbuffer.begin(6, DefaultVertexFormats.POSITION_COLOR);
			vertexbuffer.pos(0.0D, 100.0D, 0.0D).color(f6, f7, f8, afloat[3]).endVertex();
			int j = 16;

			for (int l = 0; l <= 16; ++l)
			{
				float f21 = (float)l * ((float)Math.PI * 2F) / 16.0F;
				float f12 = MathHelper.sin(f21);
				float f13 = MathHelper.cos(f21);
				vertexbuffer.pos((double)(f12 * 120.0F), (double)(f13 * 120.0F), (double)(-f13 * 40.0F * afloat[3])).color(afloat[0], afloat[1], afloat[2], 0.0F).endVertex();
			}

			tessellator.draw();
			GlStateManager.popMatrix();
			GlStateManager.shadeModel(7424);
		}

		GlStateManager.enableTexture2D();

		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		GlStateManager.pushMatrix();

		float weathereff = 1.0F - theWorld.getRainStrength(partialTicks);
		float bglight=f+f1+f2;

		GlStateManager.rotate(-90.0f, 1.0f, 0.0f, 0.0f); //e,n,z

		celestials.renderCelestial(mc, bglight, weathereff, partialTicks);

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableFog();
		GlStateManager.popMatrix();
		GlStateManager.disableTexture2D();
		GlStateManager.color(0.0F, 0.0F, 0.0F);
		double d0 = this.mc.thePlayer.getPositionEyes(partialTicks).yCoord - theWorld.getHorizon();

		if (d0 < 0.0D)
		{
			GlStateManager.pushMatrix();
			GlStateManager.translate(0.0F, 12.0F, 0.0F);

			if (this.vboEnabled)
			{
				this.sky2VBO.bindBuffer();
				GlStateManager.glEnableClientState(32884);
				GlStateManager.glVertexPointer(3, 5126, 12, 0);
				this.sky2VBO.drawArrays(7);
				this.sky2VBO.unbindBuffer();
				GlStateManager.glDisableClientState(32884);
			}
			else
			{
				GlStateManager.callList(this.glSkyList2);
			}

			GlStateManager.popMatrix();
			float f18 = 1.0F;
			float f19 = -((float)(d0 + 65.0D));
			float f20 = -1.0F;
			vertexbuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
			vertexbuffer.pos(-1.0D, (double)f19, 1.0D).color(0, 0, 0, 255).endVertex();
			vertexbuffer.pos(1.0D, (double)f19, 1.0D).color(0, 0, 0, 255).endVertex();
			vertexbuffer.pos(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
			vertexbuffer.pos(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
			vertexbuffer.pos(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
			vertexbuffer.pos(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
			vertexbuffer.pos(1.0D, (double)f19, -1.0D).color(0, 0, 0, 255).endVertex();
			vertexbuffer.pos(-1.0D, (double)f19, -1.0D).color(0, 0, 0, 255).endVertex();
			vertexbuffer.pos(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
			vertexbuffer.pos(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
			vertexbuffer.pos(1.0D, (double)f19, 1.0D).color(0, 0, 0, 255).endVertex();
			vertexbuffer.pos(1.0D, (double)f19, -1.0D).color(0, 0, 0, 255).endVertex();
			vertexbuffer.pos(-1.0D, (double)f19, -1.0D).color(0, 0, 0, 255).endVertex();
			vertexbuffer.pos(-1.0D, (double)f19, 1.0D).color(0, 0, 0, 255).endVertex();
			vertexbuffer.pos(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
			vertexbuffer.pos(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
			vertexbuffer.pos(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
			vertexbuffer.pos(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
			vertexbuffer.pos(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
			vertexbuffer.pos(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
			tessellator.draw();
		}

		if (theWorld.provider.isSkyColored())
		{
			GlStateManager.color(f * 0.2F + 0.04F, f1 * 0.2F + 0.04F, f2 * 0.6F + 0.1F);
		}
		else
		{
			GlStateManager.color(f, f1, f2);
		}

		GlStateManager.pushMatrix();
		GlStateManager.translate(0.0F, -((float)(d0 - 16.0D)), 0.0F);
		GlStateManager.callList(this.glSkyList2);
		GlStateManager.popMatrix();
		GlStateManager.enableTexture2D();
		GlStateManager.depthMask(true);
	}
}
