package stellarium.client.gui.clock;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import stellarium.StellarSkyResources;
import stellarium.client.gui.content.IRectangleBound;
import stellarium.client.gui.content.IRenderModel;
import stellarium.client.gui.content.RectangleBound;
import stellarium.client.gui.content.basicmodel.ModelSimpleTextured;
import stellarium.client.gui.content.basicmodel.ModelSimpleTexturedReflected;

public class ModelScroll implements IRenderModel {
	
	private static final ModelScroll instance = new ModelScroll();
	
	public static ModelScroll getInstance() {
		return instance;
	}
	
	private IRenderModel round, roundRefl, parallel, scroll;
	
	private RectangleBound temporal = new RectangleBound(0,0,0,0);
	private RectangleBound temporalClip = new RectangleBound(0,0,0,0);
	
	public ModelScroll() {
		this.round = new ModelSimpleTextured(StellarSkyResources.round);
		this.roundRefl = new ModelSimpleTexturedReflected(StellarSkyResources.round);
		this.parallel = new ModelSimpleTextured(StellarSkyResources.parallel);
		this.scroll = new ModelSimpleTextured(StellarSkyResources.scroll);
	}

	@Override
	public void renderModel(String info, IRectangleBound totalBound, IRectangleBound clipBound, Tessellator tessellator,
			TextureManager textureManager) {
		if(info.equals("vertical")) {
			temporal.set(totalBound);
			temporal.width = totalBound.getHeight();
			temporalClip.set(clipBound);
			temporalClip.setAsIntersection(temporal);
			round.renderModel(info, temporal, temporalClip, tessellator, textureManager);
			
			temporal.set(totalBound);
			temporal.posX = totalBound.getRightX() - totalBound.getHeight();
			temporal.width = totalBound.getHeight();
			temporalClip.set(clipBound);
			temporalClip.setAsIntersection(temporal);
			roundRefl.renderModel(info, temporal, temporalClip, tessellator, textureManager);
			
			temporal.set(totalBound);
			temporal.posX += totalBound.getHeight();
			temporal.width -= 2 * totalBound.getHeight();
			temporalClip.set(clipBound);
			temporalClip.setAsIntersection(temporal);
			parallel.renderModel(info, temporal, temporalClip, tessellator, textureManager);
			
			temporal.set(totalBound);
			temporal.posX += (totalBound.getWidth() - totalBound.getHeight())/2;
			temporal.width = totalBound.getHeight();
			temporalClip.set(clipBound);
			temporalClip.setAsIntersection(temporal);
			scroll.renderModel(info, temporal, temporalClip, tessellator, textureManager);
		} else if(info.equals("horizontal")) {
			temporal.set(totalBound);
			temporal.height = totalBound.getWidth();
			temporalClip.set(clipBound);
			temporalClip.setAsIntersection(temporal);
			round.renderModel(info, temporal, temporalClip, tessellator, textureManager);
			
			temporal.set(totalBound);
			temporal.posY = totalBound.getDownY() - totalBound.getWidth();
			temporal.height = totalBound.getWidth();
			temporalClip.set(clipBound);
			temporalClip.setAsIntersection(temporal);
			roundRefl.renderModel(info, temporal, temporalClip, tessellator, textureManager);
			
			temporal.set(totalBound);
			temporal.posY += totalBound.getWidth();
			temporal.height -= 2 * totalBound.getWidth();
			temporalClip.set(clipBound);
			temporalClip.setAsIntersection(temporal);
			parallel.renderModel(info, temporal, temporalClip, tessellator, textureManager);
			
			temporal.set(totalBound);
			temporal.posY += (totalBound.getHeight() - totalBound.getWidth())/2;
			temporal.height = totalBound.getWidth();
			temporalClip.set(clipBound);
			temporalClip.setAsIntersection(temporal);
			scroll.renderModel(info, temporal, temporalClip, tessellator, textureManager);
		}
	}

}
