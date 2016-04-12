package stellarium.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.client.IRenderHandler;
import stellarium.StellarSkyResources;
import stellarium.api.ICelestialRenderer;

public class SkyRendererEnd extends IRenderHandler {

	private ICelestialRenderer celestials;
	
	public SkyRendererEnd(ICelestialRenderer subRenderer) {
		this.celestials = subRenderer;
	}

	@Override
	public void render(float partialTicks, WorldClient world, Minecraft mc) {
		GlStateManager.disableFog();
		GlStateManager.disableAlpha();
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		RenderHelper.disableStandardItemLighting();
		GlStateManager.depthMask(false);
		mc.renderEngine.bindTexture(StellarSkyResources.resourceEndSky.getLocationFor(mc.theWorld));
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
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		
		GlStateManager.pushMatrix();
		GlStateManager.rotate(-90.0f, 1.0f, 0.0f, 0.0f); //e,n,z
		this.celestials.renderCelestial(mc, 0.0f, 1.0f, partialTicks);
		GlStateManager.popMatrix();
		
		GlStateManager.disableBlend();
		GlStateManager.depthMask(true);
		GlStateManager.enableAlpha();
	}


}
