package stellarium.render;


import java.util.List;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.IRenderHandler;
import stellarium.StellarSky;
import stellarium.client.ClientSettings;
import stellarium.stellars.StellarManager;
import stellarium.stellars.layer.CelestialManager;
import stellarium.stellars.layer.CelestialRenderer;
import stellarium.stellars.layer.ICelestialLayer;
import stellarium.stellars.view.StellarDimensionManager;

public class SkyRenderer extends IRenderHandler {

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
	private CelestialRenderer renderer;
	private boolean updated;
	
	private static final ResourceLocation locationEndSkyPng = new ResourceLocation("textures/environment/end_sky.png");
	private static final ResourceLocation locationSunPng = new ResourceLocation("stellarium", "stellar/halo.png");
	private static final ResourceLocation locationMoonPng = new ResourceLocation("stellarium", "stellar/lune.png");
	private static final ResourceLocation locationStarPng = new ResourceLocation("stellarium", "stellar/star.png");
	private static final ResourceLocation locationhalolunePng = new ResourceLocation("stellarium", "stellar/haloLune.png");

	public SkyRenderer(){
		this.mc = Minecraft.getMinecraft();
		this.renderEngine = mc.getTextureManager();
		this.vboEnabled = OpenGlHelper.useVbo();

		this.vertexBufferFormat = new VertexFormat();
		this.vertexBufferFormat.addElement(new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUsage.POSITION, 2));
		this.generateSky();
		this.generateSky2();

		//Custom Starts
		this.settings = StellarSky.proxy.getClientSettings();
		this.renderer = new CelestialRenderer();
		
		CelestialManager celManager = StellarSky.proxy.getClientCelestialManager();
		renderer.refreshRenderer(celManager.getLayers());
		settings.checkDirty();
		celManager.reloadClientSettings(this.settings);
		this.onSettingsUpdated();
		//Custom Ends
	}
	
	public void renderCelestial(StellarRenderInfo info) {
		CelestialManager manager = StellarSky.proxy.getClientCelestialManager();

		if(settings.checkDirty())
		{
			manager.reloadClientSettings(this.settings);
			this.updated = false;
		}
		
		if(!this.updated)
			this.onSettingsUpdated();
		
		renderer.render(manager.getLayers(), info);
	}
	
	private void onSettingsUpdated() {
		//Initialization update
		World world = Minecraft.getMinecraft().theWorld;
		
		if(world != null) {
			StellarManager manager = StellarManager.getManager(true);
			if(manager.getCelestialManager() != null) {
				this.updated = true;
				manager.update(world.getWorldTime());
				StellarDimensionManager dimManager = StellarDimensionManager.get(world);
				if(dimManager != null)
				{
					dimManager.update(world, world.getWorldTime());
					manager.updateClient(StellarSky.proxy.getClientSettings(),
							dimManager.getViewpoint());
				}
			}
		}
	}

	private void generateSky2()
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

	private void renderSky(WorldRenderer worldRendererIn, float p_174968_2_, boolean p_174968_3_)
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

	private void renderSkyEnd(float partialTicks)
	{
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

			worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
			worldrenderer.pos(-100.0D, -100.0D, -100.0D).tex(0.0D, 0.0D).color(40, 40, 40, 255).endVertex();
			worldrenderer.pos(-100.0D, -100.0D, 100.0D).tex(0.0D, 16.0D).color(40, 40, 40, 255).endVertex();
			worldrenderer.pos(100.0D, -100.0D, 100.0D).tex(16.0D, 16.0D).color(40, 40, 40, 255).endVertex();
			worldrenderer.pos(100.0D, -100.0D, -100.0D).tex(16.0D, 0.0D).color(40, 40, 40, 255).endVertex();
			tessellator.draw();
			GlStateManager.popMatrix();
		}

		GlStateManager.enableTexture2D();
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		
		this.renderCelestial(new StellarRenderInfo(mc, tessellator, worldrenderer, 0.0f, 1.0f, partialTicks));

		GlStateManager.disableBlend();
		GlStateManager.depthMask(true);
		GlStateManager.enableAlpha();
	}

	@Override
	public void render(float partialTicks, WorldClient theWorld, Minecraft mc)
	{
		if (theWorld.provider.getDimensionId() == 1)
		{
			this.renderSkyEnd(partialTicks);
		}
		else {
			GlStateManager.disableTexture2D();
			Vec3 vec3 = theWorld.getSkyColor(this.mc.getRenderViewEntity(), partialTicks);
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
			WorldRenderer worldrenderer = tessellator.getWorldRenderer();
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

				worldrenderer.begin(6, DefaultVertexFormats.POSITION_COLOR);
				worldrenderer.pos(0.0D, 100.0D, 0.0D).color(f6, f7, f8, afloat[3]).endVertex();
				int j = 16;

				for (int l = 0; l <= 16; ++l)
				{
					float f21 = (float)l * (float)Math.PI * 2.0F / 16.0F;
					float f12 = MathHelper.sin(f21);
					float f13 = MathHelper.cos(f21);
					worldrenderer.pos((double)(f12 * 120.0F), (double)(f13 * 120.0F), (double)(-f13 * 40.0F * afloat[3])).color(afloat[0], afloat[1], afloat[2], 0.0F).endVertex();
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

			this.renderCelestial(new StellarRenderInfo(mc, tessellator, worldrenderer, bglight, weathereff, partialTicks));

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
			GlStateManager.depthMask(true);
		}
	}
}
