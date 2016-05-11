package stellarium.client.overlay.clientcfg.model;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import stellarapi.lib.gui.IRectangleBound;
import stellarapi.lib.gui.IRenderModel;
import stellarapi.lib.gui.model.basic.ModelSimpleRect;
import stellarapi.lib.gui.model.basic.ModelSimpleTextured;
import stellarium.StellarSkyResources;

public class ModelCfgFixButton implements IRenderModel {
	
	private static final ModelCfgFixButton instance = new ModelCfgFixButton();
	
	public static ModelCfgFixButton getInstance() {
		return instance;
	}
	
	private ModelSimpleRect selectModel = ModelSimpleRect.getInstance();
	private ModelSimpleTextured clickedModel;
	private ModelSimpleTextured fixTextureModel;
	
	public ModelCfgFixButton() {
		this.clickedModel = new ModelSimpleTextured(StellarSkyResources.clicked);
		this.fixTextureModel = new ModelSimpleTextured(StellarSkyResources.fix);
	}

	@Override
	public void renderModel(String info, IRectangleBound totalBound, IRectangleBound clipBound, Tessellator tessellator,
			TextureManager textureManager, float[] color) {
		GL11.glPushMatrix();
		if(info.equals("select")) {
			GL11.glScalef(0.9f, 0.9f, 0.9f);
			color[3] *= 0.2f;
			selectModel.renderModel(info, totalBound, clipBound, tessellator, textureManager, color);
		} else if(info.equals("fixed")) {
			clickedModel.renderModel(info, totalBound, clipBound, tessellator, textureManager, color);
			GL11.glScalef(0.9f, 0.9f, 0.9f);
			fixTextureModel.renderModel(info, totalBound, clipBound, tessellator, textureManager, color);
		} else {
			GL11.glScalef(0.9f, 0.9f, 0.9f);
			GL11.glRotatef(-90.0f, 0.0f, 0.0f, 1.0f);
			fixTextureModel.renderModel(info, totalBound, clipBound, tessellator, textureManager, color);
		}
		GL11.glPopMatrix();
	}

}
