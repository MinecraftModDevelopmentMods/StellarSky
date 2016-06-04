package stellarium.stellars.system;

import stellarium.render.stellars.access.EnumStellarPass;
import stellarium.render.stellars.access.IStellarTessellator;
import stellarium.render.stellars.layer.LayerRenderInformation;
import stellarium.stellars.render.ICelestialObjectRenderer;

public enum SunRenderer implements ICelestialObjectRenderer<SunRenderCache> {
	
	INSTANCE;

	@Override
	public void render(SunRenderCache cache, EnumStellarPass pass, LayerRenderInformation info) {
		if(pass != EnumStellarPass.DominateScatter)
			return;

		IStellarTessellator tessellator = info.tessellator;
		tessellator.begin(false);
		tessellator.pos(cache.appCoord, info.deepDepth);
		tessellator.color(1.0f, 1.0f, 1.0f);
		tessellator.writeVertex();
		tessellator.end();
	}
}