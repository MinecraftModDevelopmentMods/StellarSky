package stellarium.stellars.star;

import stellarium.render.stellars.access.EnumStellarPass;
import stellarium.render.stellars.layer.LayerRI;
import stellarium.stellars.render.ICelestialObjectRenderer;

public enum StarRenderer implements ICelestialObjectRenderer<StarRenderCache> {
	
	INSTANCE;

	@Override
	public void render(StarRenderCache cache, EnumStellarPass pass, LayerRI info) {
		if(!cache.shouldRender)
			return;

		info.builder.pos(cache.pos);
		info.builder.color(cache.red * info.helper.multRed(),
				cache.green * info.helper.multGreen(),
				cache.blue * info.helper.multBlue(), 1.0f);
		info.builder.endVertex();
	}

}
