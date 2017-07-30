package stellarium.client.overlay.clientcfg.model;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import stellarapi.lib.gui.IRectangleBound;
import stellarapi.lib.gui.IRenderModel;
import stellarapi.lib.gui.model.basic.ModelSimpleRect;
import stellarapi.lib.gui.model.basic.ModelSimpleTexturedTransformed;
import stellarapi.lib.gui.model.basic.SimpleBoundTransformer;
import stellarium.StellarSkyResources;

public class ModelCfgScrollRegion implements IRenderModel {
	
	private static final ModelCfgScrollRegion instance = new ModelCfgScrollRegion();

	public static ModelCfgScrollRegion getInstance() {
		return instance;
	}

	private ModelSimpleRect selectModel = ModelSimpleRect.getInstance();
	private ModelSimpleTexturedTransformed scrollregion;
	private SimpleBoundTransformer transformer = new SimpleBoundTransformer();

	public ModelCfgScrollRegion() {
		this.scrollregion = new ModelSimpleTexturedTransformed(StellarSkyResources.scrollregion);
	}

	@Override
	public void renderModel(String info, IRectangleBound totalBound, IRectangleBound clipBound, Tessellator tessellator,
			BufferBuilder worldRenderer, TextureManager textureManager, float[] color) {
		if(info.equals("select")) {
			color[3] *= 0.2f;
			selectModel.renderModel(info, totalBound, clipBound, tessellator, worldRenderer, textureManager, color);
		} else if(info.equals("horizontal")) {
			scrollregion.setTransformer(this.transformer);
			scrollregion.renderModel(info, totalBound, clipBound, tessellator, worldRenderer, textureManager, color);
		} else if(info.equals("vertical")) {
			scrollregion.setTransformer(transformer.setRotated().setReflectedY());
			scrollregion.renderModel(info, totalBound, clipBound, tessellator, worldRenderer, textureManager, color);
		}
		transformer.reset();
	}

}
