package stellarium.render.adapt;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.client.IRenderHandler;
import stellarapi.api.render.IAdaptiveRenderer;
import stellarium.StellarSkyResources;
import stellarium.world.StellarScene;

public class SkyRendererEnd extends IAdaptiveRenderer {
	private IRenderHandler subRenderer;
	private IRenderHandler otherRenderer;

	public SkyRendererEnd(IRenderHandler subRenderer) {
		this.subRenderer = subRenderer;
	}

	@Override
	public void setReplacedRenderer(IRenderHandler handler) {
		this.otherRenderer = handler;
	}

	@Override
	public void render(float partialTicks, WorldClient theWorld, Minecraft mc) {
		GlStateManager.clearColor(0.0f, 0.0f, 0.0f, 0.0f);
		GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT);

		subRenderer.render(partialTicks, theWorld, mc);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();

		// TODO Eliminate these 'getScene'.
		StellarScene dimManager = StellarScene.getScene(theWorld);
		if(dimManager.getSettings().renderPrevSky()) {
			if(this.otherRenderer != null)
				otherRenderer.render(partialTicks, theWorld, mc);
			else {
				IRenderHandler renderer = theWorld.provider.getSkyRenderer();
				theWorld.provider.setSkyRenderer(null);
				mc.renderGlobal.renderSky(partialTicks, 0);
				theWorld.provider.setSkyRenderer(renderer);
			}
		}

		GlStateManager.disableFog();
		GlStateManager.disableAlpha();
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		RenderHelper.disableStandardItemLighting();
		GlStateManager.depthMask(false);

		if(!dimManager.getSettings().renderPrevSky()) {
			mc.renderEngine.bindTexture(StellarSkyResources.resourceEndSky.getLocation());

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

				vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
				vertexbuffer.pos(-100.0D, -100.0D, -100.0D).tex(0.0D, 0.0D).color(40, 40, 40, 127).endVertex();
				vertexbuffer.pos(-100.0D, -100.0D, 100.0D).tex(0.0D, 16.0D).color(40, 40, 40, 127).endVertex();
				vertexbuffer.pos(100.0D, -100.0D, 100.0D).tex(16.0D, 16.0D).color(40, 40, 40, 127).endVertex();
				vertexbuffer.pos(100.0D, -100.0D, -100.0D).tex(16.0D, 0.0D).color(40, 40, 40, 127).endVertex();

				tessellator.draw();
				GlStateManager.popMatrix();
			}
		}

		GlStateManager.enableTexture2D();

		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		
		GlStateManager.disableBlend();
		GlStateManager.depthMask(true);
		GlStateManager.enableAlpha();
	}
}
