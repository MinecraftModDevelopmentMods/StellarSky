package stellarium.render.stellars.phased;

import stellarapi.api.lib.config.IConfigHandler;
import stellarium.render.base.IGenericRenderer;
import stellarium.render.sky.SkyRenderInformation;
import stellarium.render.stellars.access.EnumStellarPass;
import stellarium.stellars.layer.IRenderCache;
import stellarium.stellars.layer.StellarObject;
import stellarium.stellars.layer.StellarObjectContainer;
import stellarium.stellars.render.ICelestialLayerRenderer;
import stellarium.stellars.render.ICelestialObjectRenderer;
import stellarium.stellars.render.StellarRenderingRegistry;

public class StellarPhasedRenderer implements IGenericRenderer<Void, EnumStellarPass, StellarRenderModel, StellarRenderInformation> {

	private StellarTessellator tessellator = new StellarTessellator();
	
	@Override
	public void initialize(Void settings) {
		
	}

	@Override
	public void preRender(Void settings, StellarRenderInformation info) {
		// TODO Auto-generated method stub
	}

	@Override
	public void renderPass(StellarRenderModel model, EnumStellarPass pass, StellarRenderInformation info) {
		// TODO Auto-generated method stub
		tessellator.initialize(pass, info);
		for(StellarObjectContainer<StellarObject, IConfigHandler> layer : model.getLayers()) {
			ICelestialLayerRenderer layerRenderer = null;
			
			int rendererId = layer.getType().getLayerRendererIndex();
			if(rendererId != -1)
				layerRenderer = StellarRenderingRegistry.getInstance().getLayerRenderer(rendererId);
			
			if(layerRenderer != null && !layerRenderer.acceptPass(pass))
				continue;
			
			if(layerRenderer != null)
				layerRenderer.preRender(this.tessellator, pass);
			
			for(IRenderCache cache : layer.getRenderCacheList())
			{
				ICelestialObjectRenderer objRenderer = StellarRenderingRegistry.getInstance().getObjectRenderer(cache.getRenderId());
				objRenderer.render(this.tessellator, cache);
			}
			
			if(layerRenderer != null)
				layerRenderer.postRender(this.tessellator, pass);
		}
	}

	@Override
	public void postRender(Void settings, StellarRenderInformation info) {
		// TODO Auto-generated method stub
		
	}

}
