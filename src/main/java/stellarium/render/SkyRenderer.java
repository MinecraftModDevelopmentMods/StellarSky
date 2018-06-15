package stellarium.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import stellarium.client.ClientSettings;
import stellarium.display.DisplayRenderer;
import stellarium.render.stellars.StellarRI;
import stellarium.render.stellars.StellarRenderer;
import stellarium.world.landscape.LandscapeRenderer;

public enum SkyRenderer {
	INSTANCE;

	public void initialize(ClientSettings settings) {
		StellarRenderer.INSTANCE.initialize(settings);
	}

	public void preRender(ClientSettings settings, SkyRI info) {
		StellarRenderer.INSTANCE.preRender(settings, new StellarRI(info));
	}

	public void render(SkyModel model, SkyRI info) {
		RenderHelper.disableStandardItemLighting();

		GlStateManager.pushMatrix();
		GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F); // E, N, Z coordinates

		GlStateManager.disableDepth();
		GlStateManager.depthMask(false);

		GlStateManager.disableAlpha();
		GlStateManager.disableFog();

		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		// Render display back
		DisplayRenderer.INSTANCE.render(model.displayModel, false, info);

		// Render stellars
		StellarRenderer.INSTANCE.render(model.stellarModel, new StellarRI(info));

		// Render display front
		DisplayRenderer.INSTANCE.render(model.displayModel, true, info);

		GlStateManager.enableFog();

		// Render landscape
		LandscapeRenderer.INSTANCE.render(model.landscapeModel, info);

		GlStateManager.disableFog();
		GlStateManager.enableDepth();
		GlStateManager.enableAlpha();

		GlStateManager.disableBlend();

		GlStateManager.depthMask(true);
		GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);
		GlStateManager.popMatrix();
	}

}
