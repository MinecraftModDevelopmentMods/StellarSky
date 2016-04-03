package stellarium.render;


import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IRenderHandler;

public class SkyRenderer extends IRenderHandler {

	private static final ResourceLocation locationEndSkyPng = new ResourceLocation("textures/environment/end_sky.png");
	
	private TextureManager renderEngine;
	private WorldClient world;
	private Minecraft mc;
	private Random random;
	private Tessellator tessellator1=Tessellator.instance;

	private List<ISkyRenderLayer> layers = Lists.newArrayList();

	public SkyRenderer(){
		layers.add(new SkyLayerGlow());
		layers.add(new SkyLayerCelestial());
		layers.add(new SkyLayerVoid());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void render(float partialTicks, WorldClient world, Minecraft mc) {

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

			GL11.glDepthMask(false);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			//GL11.glEnable(GL11.GL_ALPHA_TEST);

			double time=(double)world.getWorldTime()+partialTicks;

			//this.renderStar(0.0f, 0.0f, time);
			
			for(ISkyRenderLayer layer : this.layers)
				if(layer instanceof SkyLayerCelestial)
					layer.render(partialTicks, world, mc);
			
			GL11.glDepthMask(true);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
		}
		else if (mc.theWorld.provider.isSurfaceWorld())
		{
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glDepthMask(false);
			RenderHelper.disableStandardItemLighting();
			
			for(ISkyRenderLayer layer : this.layers)
				layer.render(partialTicks, world, mc);
		}

	}
}
