package stellarium.client.overlay.clock;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import stellarapi.lib.gui.IRectangleBound;
import stellarapi.lib.gui.IRenderModel;
import stellarapi.lib.gui.basicmodel.ModelSimpleRect;
import stellarapi.lib.gui.basicmodel.ModelSimpleTextured;
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
			TextureManager textureManager) {
		GL11.glPushMatrix();
		if(info.equals("select")) {
			GL11.glScalef(0.9f, 0.9f, 0.9f);
			selectModel.setColor(1.0f, 1.0f, 1.0f, 0.2f);
			selectModel.renderModel(info, totalBound, clipBound, tessellator, textureManager);
		} else if(info.equals("hhmm")) {
			GL11.glScalef(0.9f, 0.9f, 0.9f);
			hhmmModel.renderModel(info, totalBound, clipBound, tessellator, textureManager);
		} else if(info.equals("tick")) {
			GL11.glScalef(0.9f, 0.9f, 0.9f);
			tickModel.renderModel(info, totalBound, clipBound, tessellator, textureManager);
		}
		GL11.glPopMatrix();
	}

}
