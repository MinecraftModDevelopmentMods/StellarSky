package stellarium.stellars.system;

import stellarium.render.stellars.access.EnumStellarPass;
import stellarium.render.stellars.access.IStellarTessellator;
import stellarium.render.stellars.layer.LayerRenderInformation;
import stellarium.stellars.render.ICelestialObjectRenderer;

public enum PlanetRenderer implements ICelestialObjectRenderer<PlanetRenderCache> {
	
	INSTANCE;
	
	@Override
	public void render(PlanetRenderCache cache, EnumStellarPass pass, LayerRenderInformation info) {
		if(pass != EnumStellarPass.OpaqueScatter)
			return;
		
		IStellarTessellator tessellator = info.tessellator;
		
		tessellator.begin(false);
		tessellator.pos(cache.appCoord, info.deepDepth);
		tessellator.color(cache.brightness, cache.brightness, cache.brightness);
		tessellator.writeVertex();
		
		tessellator.end();
	}

}
