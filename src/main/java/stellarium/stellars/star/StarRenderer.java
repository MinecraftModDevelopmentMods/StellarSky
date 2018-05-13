package stellarium.stellars.star;

import stellarium.render.stellars.access.EnumStellarPass;
import stellarium.render.stellars.layer.LayerRenderInformation;
import stellarium.stellars.render.ICelestialObjectRenderer;

public enum StarRenderer implements ICelestialObjectRenderer<StarRenderCache> {
	
	INSTANCE;

	@Override
	public void render(StarRenderCache cache, EnumStellarPass pass, LayerRenderInformation info) {
		if(!cache.shouldRender)
			return;
		info.tessellator.color(cache.red, cache.green, cache.blue);
		info.tessellator.pos(cache.pos, info.deepDepth);
		info.tessellator.writeVertex();
	}

}
