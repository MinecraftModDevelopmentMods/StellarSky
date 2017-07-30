package stellarium.client.overlay.clientcfg.model;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import stellarapi.lib.gui.IRectangleBound;
import stellarapi.lib.gui.IRenderModel;
import stellarapi.lib.gui.RectangleBound;
import stellarapi.lib.gui.model.basic.ModelSimpleTexturedTransformed;
import stellarapi.lib.gui.model.basic.SimpleBoundTransformer;
import stellarium.StellarSkyResources;

public class ModelRollButton implements IRenderModel {
	
	private static final ModelRollButton instance = new ModelRollButton();
	
	public static ModelRollButton getInstance() {
		return instance;
	}
	
	private ModelSimpleTexturedTransformed parallel, scroll;
	private ModelSimpleTexturedTransformed round1, round2;
	
	private RectangleBound temporal = new RectangleBound(0,0,0,0);
	private RectangleBound temporalClip = new RectangleBound(0,0,0,0);
	
	private SimpleBoundTransformer transformer = new SimpleBoundTransformer();
	
	public ModelRollButton() {
		this.round1 = new ModelSimpleTexturedTransformed(StellarSkyResources.rollround);
		this.round2 = new ModelSimpleTexturedTransformed(StellarSkyResources.rollround);
		this.parallel = new ModelSimpleTexturedTransformed(StellarSkyResources.rollparallel);
		this.scroll = new ModelSimpleTexturedTransformed(StellarSkyResources.rollscroll);
	}

	/**
	 * @param info the direction of unrolling.
	 * */
	@Override
	public void renderModel(String info, IRectangleBound totalBound, IRectangleBound clipBound, Tessellator tessellator,
			BufferBuilder worldRenderer, TextureManager textureManager, float[] color) {
		if(info.equals("down") || info.equals("up")) {
			if(info.equals("down")) {
				round1.setTransformer(this.transformer);
				parallel.setTransformer(this.transformer);
				scroll.setTransformer(this.transformer);
				round2.setTransformer(transformer.setReflectedX());
			} else {
				round1.setTransformer(transformer.setReflectedY());
				parallel.setTransformer(this.transformer);
				scroll.setTransformer(this.transformer);
				round2.setTransformer(transformer.setReflectedX());
			}
			
			temporal.set(totalBound);
			temporal.width = totalBound.getHeight();
			temporalClip.set(clipBound);
			temporalClip.setAsIntersection(temporal);
			round1.renderModel(info, temporal, temporalClip, tessellator, worldRenderer, textureManager, color);
			
			temporal.set(totalBound);
			temporal.posX = totalBound.getRightX() - totalBound.getHeight();
			temporal.width = totalBound.getHeight();
			temporalClip.set(clipBound);
			temporalClip.setAsIntersection(temporal);
			round2.renderModel(info, temporal, temporalClip, tessellator, worldRenderer, textureManager, color);
			
			temporal.set(totalBound);
			temporal.posX += totalBound.getHeight();
			temporal.width -= 2 * totalBound.getHeight();
			temporalClip.set(clipBound);
			temporalClip.setAsIntersection(temporal);
			parallel.renderModel(info, temporal, temporalClip, tessellator, worldRenderer, textureManager, color);
			
			temporal.set(totalBound);
			temporal.posX += (totalBound.getWidth() - totalBound.getHeight())/2;
			temporal.width = totalBound.getHeight();
			temporalClip.set(clipBound);
			temporalClip.setAsIntersection(temporal);
			scroll.renderModel(info, temporal, temporalClip, tessellator, worldRenderer, textureManager, color);
		} else if(info.equals("right") || info.equals("left")) {
			if(info.equals("left")) {
				parallel.setTransformer(transformer.setRotated());
				scroll.setTransformer(this.transformer.setReflectedY());
				round1.setTransformer(this.transformer);
				round2.setTransformer(transformer.setReflectedY());
			} else {
				parallel.setTransformer(transformer.setRotated().setReflectedY());
				scroll.setTransformer(this.transformer);
				round1.setTransformer(this.transformer);
				round2.setTransformer(transformer.setReflectedY());
			}
			
			temporal.set(totalBound);
			temporal.height = totalBound.getWidth();
			temporalClip.set(clipBound);
			temporalClip.setAsIntersection(temporal);
			round1.renderModel(info, temporal, temporalClip, tessellator, worldRenderer, textureManager, color);
			
			temporal.set(totalBound);
			temporal.posY = totalBound.getDownY() - totalBound.getWidth();
			temporal.height = totalBound.getWidth();
			temporalClip.set(clipBound);
			temporalClip.setAsIntersection(temporal);
			round2.renderModel(info, temporal, temporalClip, tessellator,worldRenderer,  textureManager, color);
			
			temporal.set(totalBound);
			temporal.posY += totalBound.getWidth();
			temporal.height -= 2 * totalBound.getWidth();
			temporalClip.set(clipBound);
			temporalClip.setAsIntersection(temporal);
			parallel.renderModel(info, temporal, temporalClip, tessellator, worldRenderer, textureManager, color);
			
			temporal.set(totalBound);
			temporal.posY += (totalBound.getHeight() - totalBound.getWidth())/2;
			temporal.height = totalBound.getWidth();
			temporalClip.set(clipBound);
			temporalClip.setAsIntersection(temporal);
			scroll.renderModel(info, temporal, temporalClip, tessellator, worldRenderer, textureManager, color);
		}
		
		transformer.reset();
	}

}
