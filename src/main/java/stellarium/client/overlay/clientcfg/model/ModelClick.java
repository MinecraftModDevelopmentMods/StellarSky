package stellarium.client.overlay.clientcfg.model;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureManager;
import stellarapi.lib.gui.IRectangleBound;
import stellarapi.lib.gui.IRenderModel;
import stellarapi.lib.gui.model.basic.ModelSimpleRect;
import stellarapi.lib.gui.model.basic.ModelSimpleTextured;
import stellarium.StellarSkyResources;

public class ModelClick implements IRenderModel {
	
	private static final ModelClick instance = new ModelClick();
	
	public static ModelClick getInstance() {
		return instance;
	}
	
	private ModelSimpleRect selectModel = ModelSimpleRect.getInstance();
	private ModelSimpleTextured clickedModel;
	
	public ModelClick() {
		this.clickedModel = new ModelSimpleTextured(StellarSkyResources.clicked);
	}

	@Override
	public void renderModel(String info, IRectangleBound totalBound, IRectangleBound clipBound, Tessellator tessellator,
			VertexBuffer worldRenderer, TextureManager textureManager, float[] color) {
		
		GL11.glPushMatrix();
		if(info.contains("select")) {
			GL11.glScalef(0.9f, 0.9f, 0.9f);
			color[3] *= 0.15f;
			selectModel.renderModel(info, totalBound, clipBound, tessellator, worldRenderer, textureManager, color);
		}
		GL11.glPopMatrix();
	}

}
