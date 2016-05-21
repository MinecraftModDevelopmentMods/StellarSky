package stellarium.client.overlay.clientcfg.model;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureManager;
import stellarapi.lib.gui.IRectangleBound;
import stellarapi.lib.gui.IRenderModel;
import stellarapi.lib.gui.RectangleBound;
import stellarapi.lib.gui.model.basic.ModelSimpleTexturedTransformed;
import stellarapi.lib.gui.model.basic.SimpleBoundTransformer;
import stellarium.StellarSkyResources;

public class ModelBackground implements IRenderModel {
	
	private static final ModelBackground instance = new ModelBackground();
	
	public static ModelBackground getInstance() {
		return instance;
	}
	
	private ModelSimpleTexturedTransformed parallel;
	
	private RectangleBound temporal = new RectangleBound(0,0,0,0);
	private RectangleBound temporalClip = new RectangleBound(0,0,0,0);
	
	private SimpleBoundTransformer transformer = new SimpleBoundTransformer();
	
	public ModelBackground() {
		this.parallel = new ModelSimpleTexturedTransformed(StellarSkyResources.background);
	}

	/**
	 * @param info the direction of unrolling.
	 * */
	@Override
	public void renderModel(String info, IRectangleBound totalBound, IRectangleBound clipBound, Tessellator tessellator,
			VertexBuffer worldRenderer, TextureManager textureManager, float[] color) {
		if(info.equals("down") || info.equals("up")) {
			if(info.equals("down")) {
				parallel.setTransformer(transformer.setRotated());
			} else {
				parallel.setTransformer(transformer.setRotated().setReflectedY());
			}

			parallel.renderModel(info, totalBound, clipBound, tessellator, worldRenderer, textureManager, color);
		} else if(info.equals("right") || info.equals("left")) {
			if(info.equals("left")) {
				parallel.setTransformer(transformer.setReflectedX());
			} else {
				parallel.setTransformer(this.transformer);
			}

			parallel.renderModel(info, totalBound, clipBound, tessellator, worldRenderer, textureManager, color);
		}
		
		transformer.reset();
	}

}
