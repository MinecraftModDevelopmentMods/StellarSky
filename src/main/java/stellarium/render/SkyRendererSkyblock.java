package stellarium.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.IRenderHandler;
import stellarium.api.ICelestialRenderer;
import stellarium.client.ClientSettings;

public class SkyRendererSkyblock extends IRenderHandler {

	private TextureManager renderEngine;
	private Minecraft mc;
	private Tessellator tessellator1=Tessellator.getInstance();

	private VertexFormat vertexBufferFormat;
	private VertexBuffer skyVBO;
	private VertexBuffer sky2VBO;
	private boolean vboEnabled = false;

	private int glSkyList = -1;
	private int glSkyList2 = -1;

	private ClientSettings settings;
	private boolean updated;
	
    private int skyList;
    private int skyListUnderPlayer;
    
    private ICelestialRenderer celestials;

	public SkyRendererSkyblock(ICelestialRenderer subRenderer) {
		this.mc = Minecraft.getMinecraft();
		this.renderEngine = mc.getTextureManager();
		this.vboEnabled = OpenGlHelper.useVbo();
		
		this.vertexBufferFormat = new VertexFormat();
		this.vertexBufferFormat.addElement(new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUsage.POSITION, 2));
		this.generateSky();
		this.generateSky2();
		
		this.celestials = subRenderer;
	}

	private void generateSky2()
	{
		Tessellator tessellator = Tessellator.getInstance();
		net.minecraft.client.renderer.VertexBuffer worldrenderer = tessellator.getBuffer();

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
			this.renderSky(worldrenderer, -16.0F, true);
			worldrenderer.finishDrawing();
			worldrenderer.reset();
			this.sky2VBO.bufferData(worldrenderer.getByteBuffer());
		}
		else
		{
			this.glSkyList2 = GLAllocation.generateDisplayLists(1);
			GL11.glNewList(this.glSkyList2, GL11.GL_COMPILE);
			this.renderSky(worldrenderer, -16.0F, true);
			tessellator.draw();
			GL11.glEndList();
		}
	}

	private void generateSky()
	{
		Tessellator tessellator = Tessellator.getInstance();
		net.minecraft.client.renderer.VertexBuffer worldrenderer = tessellator.getBuffer();

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
			this.renderSky(worldrenderer, 16.0F, false);
			worldrenderer.finishDrawing();
			worldrenderer.reset();
			this.skyVBO.bufferData(worldrenderer.getByteBuffer());
		}
		else
		{
			this.glSkyList = GLAllocation.generateDisplayLists(1);
			GL11.glNewList(this.glSkyList, GL11.GL_COMPILE);
			this.renderSky(worldrenderer, 16.0F, false);
			tessellator.draw();
			GL11.glEndList();
		}
	}

	private void renderSky(net.minecraft.client.renderer.VertexBuffer worldRendererIn, float p_174968_2_, boolean p_174968_3_)
	{
		int i = 64;
		int j = 6;
		worldRendererIn.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);

		for (int k = -384; k <= 384; k += 64)
		{
			for (int l = -384; l <= 384; l += 64)
			{
				float f = (float)k;
				float f1 = (float)(k + 64);

				if (p_174968_3_)
				{
					f1 = (float)k;
					f = (float)(k + 64);
				}

				worldRendererIn.pos((double)f, (double)p_174968_2_, (double)l).endVertex();
				worldRendererIn.pos((double)f1, (double)p_174968_2_, (double)l).endVertex();
				worldRendererIn.pos((double)f1, (double)p_174968_2_, (double)(l + 64)).endVertex();
				worldRendererIn.pos((double)f, (double)p_174968_2_, (double)(l + 64)).endVertex();
			}
		}
	}

	@Override
	public void render(float partialTicks, WorldClient theWorld, Minecraft mc)
	{
		GlStateManager.disableTexture2D();
		Vec3d vec3 = theWorld.getSkyColor(this.mc.getRenderViewEntity(), partialTicks);
		float f = (float)vec3.xCoord;
		float f1 = (float)vec3.yCoord;
		float f2 = (float)vec3.zCoord;

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
		net.minecraft.client.renderer.VertexBuffer worldrenderer = tessellator.getBuffer();
		GlStateManager.depthMask(false);
		GlStateManager.enableFog();
		GlStateManager.color(f, f1, f2);

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
			GL11.glCallList(this.glSkyList);
		}

		GlStateManager.disableFog();
		GlStateManager.disableAlpha();
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		RenderHelper.disableStandardItemLighting();
		float[] afloat = theWorld.provider.calcSunriseSunsetColors(theWorld.getCelestialAngle(partialTicks), partialTicks);

		if (afloat != null)
			celestials.renderSunriseSunsetEffect(mc, theWorld, afloat, partialTicks);

		GlStateManager.enableTexture2D();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		GlStateManager.pushMatrix();

        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F); // e,n,z
        
		celestials.renderCelestial(mc, theWorld, new float[]{f, f1, f2}, partialTicks);

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
			float f18 = 1.0F;
			float f19 = -((float)(d0 + 65.0D));
			float f20 = -1.0F;
			worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
			worldrenderer.pos(-1.0D, (double)f19, 1.0D).color(0, 0, 0, 255).endVertex();
			worldrenderer.pos(1.0D, (double)f19, 1.0D).color(0, 0, 0, 255).endVertex();
			worldrenderer.pos(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
			worldrenderer.pos(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
			worldrenderer.pos(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
			worldrenderer.pos(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
			worldrenderer.pos(1.0D, (double)f19, -1.0D).color(0, 0, 0, 255).endVertex();
			worldrenderer.pos(-1.0D, (double)f19, -1.0D).color(0, 0, 0, 255).endVertex();
			worldrenderer.pos(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
			worldrenderer.pos(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
			worldrenderer.pos(1.0D, (double)f19, 1.0D).color(0, 0, 0, 255).endVertex();
			worldrenderer.pos(1.0D, (double)f19, -1.0D).color(0, 0, 0, 255).endVertex();
			worldrenderer.pos(-1.0D, (double)f19, -1.0D).color(0, 0, 0, 255).endVertex();
			worldrenderer.pos(-1.0D, (double)f19, 1.0D).color(0, 0, 0, 255).endVertex();
			worldrenderer.pos(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
			worldrenderer.pos(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
			worldrenderer.pos(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
			worldrenderer.pos(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
			worldrenderer.pos(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
			worldrenderer.pos(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
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

		float color = (float) (1.0 + d0 / theWorld.getHorizon());

		GlStateManager.disableFog();
		GlStateManager.pushMatrix();
		GlStateManager.translate(0.0F, -((float)d0), 0.0F);
		GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F); // e,n,z
		GlStateManager.color(color, color, color);
		celestials.renderSkyLandscape(mc, theWorld, partialTicks);
		GlStateManager.popMatrix();
		GlStateManager.enableFog();

		GlStateManager.depthMask(true);
	}
}
