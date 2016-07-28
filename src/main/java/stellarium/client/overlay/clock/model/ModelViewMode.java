package stellarium.client.overlay.clock.model;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureManager;
import stellarapi.lib.gui.IRectangleBound;
import stellarapi.lib.gui.IRenderModel;
import stellarapi.lib.gui.model.basic.ModelSimpleRect;
import stellarapi.lib.gui.model.basic.ModelSimpleTextured;
import stellarium.StellarSkyResources;

public class ModelViewMode implements IRenderModel {
	
	private static final ModelViewMode instance = new ModelViewMode();
	
	public static ModelViewMode getInstance() {
		return instance;
	}

	private ModelSimpleRect selectModel = ModelSimpleRect.getInstance();
	private ModelSimpleTextured clickedModel;
	private ModelSimpleTextured hhmmModel, tickModel, ampmModel;

	public ModelViewMode() {
		this.clickedModel = new ModelSimpleTextured(StellarSkyResources.clicked);
		this.hhmmModel = new ModelSimpleTextured(StellarSkyResources.hhmm);
		this.tickModel = new ModelSimpleTextured(StellarSkyResources.tick);
		this.ampmModel = new ModelSimpleTextured(StellarSkyResources.ampm);
	}

	@Override
	public void renderModel(String info, IRectangleBound totalBound, IRectangleBound clipBound, Tessellator tessellator,
			VertexBuffer worldRenderer, TextureManager textureManager, float[] color) {
		GlStateManager.pushMatrix();
		if(info.equals("select")) {
			GlStateManager.scale(0.9f, 0.9f, 0.9f);
			color[3] *= 0.2f;
			selectModel.renderModel(info, totalBound, clipBound, tessellator, worldRenderer, textureManager, color);
		} else if(info.equals("hhmm")) {
			GlStateManager.scale(0.9f, 0.9f, 0.9f);
			hhmmModel.renderModel(info, totalBound, clipBound, tessellator, worldRenderer, textureManager, color);
		} else if(info.equals("tick")) {
			GlStateManager.scale(0.9f, 0.9f, 0.9f);
			tickModel.renderModel(info, totalBound, clipBound, tessellator, worldRenderer, textureManager, color);
		} else if(info.equals("ampm")) {
			GlStateManager.scale(0.9f, 0.9f, 0.9f);
			ampmModel.renderModel(info, totalBound, clipBound, tessellator, worldRenderer, textureManager, color);
		}
		GlStateManager.popMatrix();
	}

}
