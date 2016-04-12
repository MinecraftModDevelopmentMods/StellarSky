package stellarium.stellars.star;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import stellarium.StellarSkyResources;
import stellarium.render.ICelestialLayerRenderer;
import stellarium.render.StellarRenderInfo;

public class LayerStarRenderer implements ICelestialLayerRenderer {

	@Override
	public void preRender(StellarRenderInfo info) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, info.weathereff);

		info.mc.renderEngine.bindTexture(StellarSkyResources.resourceStar.getLocationFor(info.mc.theWorld));
		info.worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
	}

	@Override
	public void postRender(StellarRenderInfo info) {
		info.tessellator.draw();
	}

}
