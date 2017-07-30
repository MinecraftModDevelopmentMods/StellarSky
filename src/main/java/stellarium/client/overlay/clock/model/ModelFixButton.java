package stellarium.client.overlay.clock.model;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import stellarapi.lib.gui.IRectangleBound;
import stellarapi.lib.gui.IRenderModel;
import stellarapi.lib.gui.model.basic.ModelSimpleRect;
import stellarapi.lib.gui.model.basic.ModelSimpleTextured;
import stellarium.StellarSkyResources;

public class ModelFixButton implements IRenderModel {
	
	private static final ModelFixButton instance = new ModelFixButton();
	
	public static ModelFixButton getInstance() {
		return instance;
	}
	
	private ModelSimpleRect selectModel = ModelSimpleRect.getInstance();
	private ModelSimpleTextured clickedModel;
	private ModelSimpleTextured fixTextureModel;
	
	public ModelFixButton() {
		this.clickedModel = new ModelSimpleTextured(StellarSkyResources.clicked);
		this.fixTextureModel = new ModelSimpleTextured(StellarSkyResources.fix);
	}

	@Override
	public void renderModel(String info, IRectangleBound totalBound, IRectangleBound clipBound, Tessellator tessellator,
			BufferBuilder worldRenderer, TextureManager textureManager, float[] color) {
		GlStateManager.pushMatrix();
		if(info.equals("select")) {
			GlStateManager.scale(0.9f, 0.9f, 0.9f);
			color[3] *= 0.2f;
			selectModel.renderModel(info, totalBound, clipBound, tessellator, worldRenderer, textureManager, color);
		} else if(info.equals("fixed")) {
			clickedModel.renderModel(info, totalBound, clipBound, tessellator, worldRenderer, textureManager, color);
			GlStateManager.scale(0.9f, 0.9f, 0.9f);
			fixTextureModel.renderModel(info, totalBound, clipBound, tessellator, worldRenderer, textureManager, color);
		} else {
			GlStateManager.scale(0.9f, 0.9f, 0.9f);
			GlStateManager.rotate(-90.0f, 0.0f, 0.0f, 1.0f);
			fixTextureModel.renderModel(info, totalBound, clipBound, tessellator, worldRenderer, textureManager, color);
		}
		GlStateManager.popMatrix();
	}

}
