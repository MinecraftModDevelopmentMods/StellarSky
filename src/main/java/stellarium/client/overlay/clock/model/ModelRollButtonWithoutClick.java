package stellarium.client.overlay.clock.model;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureManager;
import stellarapi.lib.gui.IRectangleBound;
import stellarapi.lib.gui.IRenderModel;
import stellarapi.lib.gui.RectangleBound;
import stellarapi.lib.gui.model.basic.ModelSimpleTextured;
import stellarapi.lib.gui.model.basic.ModelSimpleTexturedTransformed;
import stellarapi.lib.gui.model.basic.SimpleBoundTransformer;
import stellarium.StellarSkyResources;

public class ModelRollButtonWithoutClick implements IRenderModel {
	
	private static final ModelRollButtonWithoutClick instance = new ModelRollButtonWithoutClick();
	
	public static ModelRollButtonWithoutClick getInstance() {
		return instance;
	}
	
	private IRenderModel round, parallel, scroll;
	private ModelSimpleTexturedTransformed roundRefl;
	
	private RectangleBound temporal = new RectangleBound(0,0,0,0);
	private RectangleBound temporalClip = new RectangleBound(0,0,0,0);
	
	private SimpleBoundTransformer transformer = new SimpleBoundTransformer();
	
	public ModelRollButtonWithoutClick() {
		this.round = new ModelSimpleTextured(StellarSkyResources.round);
		this.roundRefl = new ModelSimpleTexturedTransformed(StellarSkyResources.round);
		this.parallel = new ModelSimpleTextured(StellarSkyResources.parallel);
		this.scroll = new ModelSimpleTextured(StellarSkyResources.scroll);
	}

	@Override
	public void renderModel(String info, IRectangleBound totalBound, IRectangleBound clipBound, Tessellator tessellator,
			VertexBuffer worldRenderer, TextureManager textureManager, float[] color) {
		if(info.equals("vertical")) {
			roundRefl.setTransformer(transformer.setReflectedX());
			
			temporal.set(totalBound);
			temporal.width = totalBound.getHeight();
			temporalClip.set(clipBound);
			temporalClip.setAsIntersection(temporal);
			round.renderModel(info, temporal, temporalClip, tessellator, worldRenderer, textureManager, color);
			
			temporal.set(totalBound);
			temporal.posX = totalBound.getRightX() - totalBound.getHeight();
			temporal.width = totalBound.getHeight();
			temporalClip.set(clipBound);
			temporalClip.setAsIntersection(temporal);
			roundRefl.renderModel(info, temporal, temporalClip, tessellator, worldRenderer, textureManager, color);
			
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
			
			transformer.setReflectedX();
		}
	}

}
