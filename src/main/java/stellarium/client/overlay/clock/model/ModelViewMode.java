package stellarium.client.overlay.clock.model;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;
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
	private ModelSimpleTextured hhmmModel, tickModel;
	
	public ModelViewMode() {
		this.clickedModel = new ModelSimpleTextured(StellarSkyResources.clicked);
		this.hhmmModel = new ModelSimpleTextured(StellarSkyResources.hhmm);
		this.tickModel = new ModelSimpleTextured(StellarSkyResources.tick);
	}

	@Override
	public void renderModel(String info, IRectangleBound totalBound, IRectangleBound clipBound, Tessellator tessellator,
			TextureManager textureManager, float[] color) {
		GL11.glPushMatrix();
		if(info.equals("select")) {
			GL11.glScalef(0.9f, 0.9f, 0.9f);
			color[3] *= 0.2f;
			selectModel.renderModel(info, totalBound, clipBound, tessellator, textureManager, color);
		} else if(info.equals("hhmm")) {
			GL11.glScalef(0.9f, 0.9f, 0.9f);
			hhmmModel.renderModel(info, totalBound, clipBound, tessellator, textureManager, color);
		} else if(info.equals("tick")) {
			GL11.glScalef(0.9f, 0.9f, 0.9f);
			tickModel.renderModel(info, totalBound, clipBound, tessellator, textureManager, color);
		}
		GL11.glPopMatrix();
	}

}
