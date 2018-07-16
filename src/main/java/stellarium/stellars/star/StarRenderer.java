package stellarium.stellars.star;

import stellarium.render.stellars.access.EnumStellarPass;
import stellarium.render.stellars.layer.LayerRHelper;
import stellarium.stellars.OpticsHelper;
import stellarium.stellars.render.ICelestialObjectRenderer;

public enum StarRenderer implements ICelestialObjectRenderer<StarRenderCache> {
	INSTANCE;

	@Override
	public void render(StarRenderCache cache, EnumStellarPass pass, LayerRHelper info) {
		float multiplier = OpticsHelper.getMultFromArea(info.pointArea());
		info.renderPoint(cache.pos, LayerRHelper.DEEP_DEPTH,
				cache.red * multiplier, cache.green * multiplier, cache.blue * multiplier);
	}

}
