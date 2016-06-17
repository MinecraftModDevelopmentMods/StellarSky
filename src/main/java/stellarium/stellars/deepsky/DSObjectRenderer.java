package stellarium.stellars.deepsky;

import stellarium.render.stellars.access.EnumStellarPass;
import stellarium.render.stellars.access.IStellarTessellator;
import stellarium.render.stellars.layer.LayerRenderInformation;
import stellarium.stellars.render.ICelestialObjectRenderer;

public enum DSObjectRenderer implements ICelestialObjectRenderer<DeepSkyObjectCache> {

	INSTANCE;

	@Override
	public void render(DeepSkyObjectCache cache, EnumStellarPass pass, LayerRenderInformation info) {
		if(!cache.shouldRender)
			return;
		
		IStellarTessellator tessellator = info.tessellator;
		
		tessellator.bindTexture(cache.location);

		tessellator.begin(false);
		tessellator.pos(cache.coords[0], info.deepDepth);
		tessellator.color(cache.alpha, cache.alpha, cache.alpha);
		tessellator.texture(1, 0);
		tessellator.writeVertex();
		
		tessellator.pos(cache.coords[1], info.deepDepth);
		tessellator.color(cache.alpha, cache.alpha, cache.alpha);
		tessellator.texture(0, 0);
		tessellator.writeVertex();
		
		tessellator.pos(cache.coords[2], info.deepDepth);
		tessellator.color(cache.alpha, cache.alpha, cache.alpha);
		tessellator.texture(0, 1);
		tessellator.writeVertex();
		
		tessellator.pos(cache.coords[3], info.deepDepth);
		tessellator.color(cache.alpha, cache.alpha, cache.alpha);
		tessellator.texture(1, 1);
		tessellator.writeVertex();

		tessellator.end();
	}

}
