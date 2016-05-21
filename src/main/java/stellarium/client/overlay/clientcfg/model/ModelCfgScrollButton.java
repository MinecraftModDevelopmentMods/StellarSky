package stellarium.client.overlay.clientcfg.model;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureManager;
import stellarapi.lib.gui.IRectangleBound;
import stellarapi.lib.gui.IRenderModel;
import stellarapi.lib.gui.model.basic.ModelSimpleRect;
import stellarapi.lib.gui.model.basic.ModelSimpleTextured;
import stellarium.StellarSkyResources;

public class ModelCfgScrollButton implements IRenderModel {
	
	private static final ModelCfgScrollButton instance = new ModelCfgScrollButton();
	
	public static ModelCfgScrollButton getInstance() {
		return instance;
	}
	
	private ModelSimpleRect selectModel = ModelSimpleRect.getInstance();
	private ModelSimpleTextured scrollbtn;
	
	public ModelCfgScrollButton() {
		this.scrollbtn = new ModelSimpleTextured(StellarSkyResources.scrollbtn);
	}

	@Override
	public void renderModel(String info, IRectangleBound totalBound, IRectangleBound clipBound, Tessellator tessellator,
			VertexBuffer worldRenderer, TextureManager textureManager, float[] color) {
		GlStateManager.pushMatrix();
		if(info.equals("select")) {
			GlStateManager.scale(0.5f, 0.5f, 0.5f);
			color[3] *= 0.2f;
			selectModel.renderModel(info, totalBound, clipBound, tessellator, worldRenderer, textureManager, color);
		} else if(info.equals("button")) {
			scrollbtn.renderModel(info, totalBound, clipBound, tessellator, worldRenderer, textureManager, color);
		}
		GlStateManager.popMatrix();
	}

}
