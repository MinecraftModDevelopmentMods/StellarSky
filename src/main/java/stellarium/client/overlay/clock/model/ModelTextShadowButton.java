package stellarium.client.overlay.clock.model;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureManager;
import stellarapi.lib.gui.IRectangleBound;
import stellarapi.lib.gui.IRenderModel;
import stellarapi.lib.gui.model.basic.ModelSimpleRect;
import stellarapi.lib.gui.model.basic.ModelSimpleTextured;
import stellarapi.lib.gui.model.font.ModelFont;
import stellarapi.lib.gui.model.font.TextStyle;
import stellarium.StellarSkyResources;

public class ModelTextShadowButton implements IRenderModel {
	
	private static final ModelTextShadowButton instance = new ModelTextShadowButton();
	
	public static ModelTextShadowButton getInstance() {
		return instance;
	}
	
	private ModelSimpleRect selectModel = ModelSimpleRect.getInstance();
	private ModelSimpleTextured clickedModel;
	private ModelFont font;
	private TextStyle style = new TextStyle();
	
	public ModelTextShadowButton() {
		this.clickedModel = new ModelSimpleTextured(StellarSkyResources.clicked);
		this.font = new ModelFont(true);
	}

	@Override
	public void renderModel(String info, IRectangleBound totalBound, IRectangleBound clipBound, Tessellator tessellator,
			VertexBuffer worldRenderer, TextureManager textureManager, float[] color) {
		GlStateManager.pushMatrix();
		if(info.equals("select")) {
			GlStateManager.scale(0.9f, 0.9f, 0.9f);
			color[3] *= 0.2f;
			selectModel.renderModel(info, totalBound, clipBound, tessellator, worldRenderer, textureManager, color);
		} else if(info.equals("shadow")) {
			font.setStyle(style.setShaded(true));
			clickedModel.renderModel(info, totalBound, clipBound, tessellator, worldRenderer, textureManager, color);
			font.renderModel("T", totalBound, clipBound, tessellator, worldRenderer, textureManager, color);
		} else {
			font.setStyle(style.setShaded(false));
			font.renderModel("T", totalBound, clipBound, tessellator, worldRenderer, textureManager, color);
		}
		GlStateManager.popMatrix();
	}

}
