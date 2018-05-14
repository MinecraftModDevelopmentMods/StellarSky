package stellarium.stellars.system;

import stellarium.render.stellars.access.EnumStellarPass;
import stellarium.render.stellars.access.IStellarTessellator;
import stellarium.render.stellars.layer.LayerRI;
import stellarium.stellars.render.ICelestialObjectRenderer;

public enum PlanetRenderer implements ICelestialObjectRenderer<PlanetRenderCache> {
	
	INSTANCE;
	
	@Override
	public void render(PlanetRenderCache cache, EnumStellarPass pass, LayerRI info) {
		if(pass != EnumStellarPass.OpaqueScatter || !cache.shouldRender)
			return;

		IStellarTessellator tessellator = info.tessellator;

		tessellator.begin(false);
		info.tessellator.pos(cache.pos, info.deepDepth);
		tessellator.color(cache.brightness, cache.brightness, cache.brightness);
		tessellator.radius(cache.size);
		tessellator.writeVertex();

		tessellator.end();
	}

}
