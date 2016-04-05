package stellarium.stellars.star;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import stellarium.render.StellarRenderInfo;
import stellarium.stellars.layer.ICelestialLayerRenderer;

public class LayerStarRenderer implements ICelestialLayerRenderer {
	
	private static final ResourceLocation locationStarPng = new ResourceLocation("stellarium", "stellar/star.png");

	@Override
	public void preRender(StellarRenderInfo info) {
		info.mc.renderEngine.bindTexture(locationStarPng);
		info.worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
	}

	@Override
	public void postRender(StellarRenderInfo info) {
		info.tessellator.draw();
	}

}
