package stellarium.stellars.system;

import stellarium.StellarSkyResources;
import stellarium.render.stellars.access.EnumStellarPass;
import stellarium.render.stellars.access.IStellarTessellator;
import stellarium.render.stellars.layer.LayerRenderInformation;
import stellarium.stellars.render.ICelestialObjectRenderer;

public enum MoonRenderer implements ICelestialObjectRenderer<MoonRenderCache> {
	INSTANCE;

	@Override
	public void render(MoonRenderCache cache, EnumStellarPass pass, LayerRenderInformation info) {
		IStellarTessellator tessellator = info.tessellator;
		if(pass == EnumStellarPass.DominateScatter && cache.shouldRenderDominate) {
			tessellator.begin(false);
			tessellator.pos(cache.appCoord, info.deepDepth);
			tessellator.color(cache.domination, cache.domination, cache.domination);
			tessellator.writeVertex();
			tessellator.end();
		} else if(pass == EnumStellarPass.OpaqueScatter && cache.shouldRender) {
			/*tessellator.begin(false);
			tessellator.radius(cache.size);
			tessellator.pos(cache.appCoord, info.deepDepth);
			tessellator.color(cache.brightness, cache.brightness, cache.brightness);
			tessellator.writeVertex();
			tessellator.end();*/
		} else if(pass == EnumStellarPass.Opaque && cache.shouldRender) {
			tessellator.bindTexture(StellarSkyResources.resourceMoonSurface.getLocation());
			tessellator.begin(true);
			tessellator.radius(cache.size);

			int longc, latc;

			for(longc=0; longc<cache.longn; longc++){
				for(latc=0; latc<cache.latn; latc++){
					int longcd=(longc+1)%cache.longn;
					float longd=(float)longc/(float)cache.longn + 0.5f;
					float latd=1.0f-(float)latc/(float)cache.latn;
					float longdd=(float)(longc+1)/(float)cache.longn + 0.5f;
					float latdd=1.0f-(float)(latc+1)/(float)cache.latn;

					tessellator.pos(cache.moonPos[longc][latc], info.deepDepth / 2.0f);
					tessellator.texture(longd, latd);
					tessellator.color(cache.moonilum[longc][latc], cache.moonilum[longc][latc], cache.moonilum[longc][latc]);
					tessellator.normal(cache.moonnormal[longc][latc]);
					tessellator.writeVertex();
					
					tessellator.pos(cache.moonPos[longcd][latc], info.deepDepth / 2.0f);
					tessellator.texture(longdd, latd);
					tessellator.color(cache.moonilum[longcd][latc], cache.moonilum[longcd][latc], cache.moonilum[longcd][latc]);
					tessellator.normal(cache.moonnormal[longcd][latc]);
					tessellator.writeVertex();
					
					tessellator.pos(cache.moonPos[longcd][latc+1], info.deepDepth / 2.0f);
					tessellator.texture(longdd, latdd);
					tessellator.color(cache.moonilum[longcd][latc+1], cache.moonilum[longcd][latc+1], cache.moonilum[longcd][latc+1]);
					tessellator.normal(cache.moonnormal[longcd][latc+1]);
					tessellator.writeVertex();
					
					tessellator.pos(cache.moonPos[longc][latc+1], info.deepDepth / 2.0f);
					tessellator.texture(longd, latdd);
					tessellator.color(cache.moonilum[longc][latc+1], cache.moonilum[longc][latc+1], cache.moonilum[longc][latc+1]);
					tessellator.normal(cache.moonnormal[longc][latc+1]);
					tessellator.writeVertex();
				}
			}
			
			tessellator.end();
		}
	}

}
