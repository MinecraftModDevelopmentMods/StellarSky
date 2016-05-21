package stellarium.client.overlay.clock.model;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureManager;
import stellarapi.lib.gui.IRectangleBound;
import stellarapi.lib.gui.IRenderModel;
import stellarapi.lib.gui.model.basic.ModelSimpleRect;
import stellarapi.lib.gui.model.basic.ModelSimpleTextured;
import stellarium.StellarSkyResources;

public class ModelScrollRegion implements IRenderModel {
	
	private static final ModelScrollRegion instance = new ModelScrollRegion();
	
	public static ModelScrollRegion getInstance() {
		return instance;
	}
	
	private ModelSimpleRect selectModel = ModelSimpleRect.getInstance();
	private ModelSimpleTextured scrollregion;
	
	public ModelScrollRegion() {
		this.scrollregion = new ModelSimpleTextured(StellarSkyResources.scrollregion);
	}

	@Override
	public void renderModel(String info, IRectangleBound totalBound, IRectangleBound clipBound, Tessellator tessellator,
			VertexBuffer worldRenderer, TextureManager textureManager, float[] color) {
		if(info.equals("select")) {
			color[3] *= 0.2f;
			selectModel.renderModel(info, totalBound, clipBound, tessellator, worldRenderer, textureManager, color);
		} else if(info.equals("region")) {
			scrollregion.renderModel(info, totalBound, clipBound, tessellator, worldRenderer, textureManager, color);
		}
	}

}
