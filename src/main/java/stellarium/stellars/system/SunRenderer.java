package stellarium.stellars.system;

import stellarium.StellarSkyResources;
import stellarium.render.stellars.access.EnumStellarPass;
import stellarium.render.stellars.access.IStellarTessellator;
import stellarium.render.stellars.layer.LayerRenderInformation;
import stellarium.stellars.render.ICelestialObjectRenderer;

public enum SunRenderer implements ICelestialObjectRenderer<SunRenderCache> {
	
	INSTANCE;

	@Override
	public void render(SunRenderCache cache, EnumStellarPass pass, LayerRenderInformation info) {
		IStellarTessellator tessellator = info.tessellator;

		if(pass == EnumStellarPass.DominateScatter) {
			tessellator.begin(false);
			tessellator.pos(cache.appCoord, info.deepDepth);
			tessellator.color(1.0f, 1.0f, 1.0f);
			tessellator.writeVertex();
			tessellator.end();
		} else if(pass == EnumStellarPass.Opaque) {
			tessellator.bindTexture(StellarSkyResources.resourceSunSurface.getLocation());
			tessellator.begin(true);
			tessellator.radius(cache.size);

			int longc, latc;

			for(longc=0; longc<cache.longn; longc++){
				for(latc=0; latc<cache.latn; latc++){
					int longcd=(longc+1)%cache.longn;
					float longd=(float)longc/(float)cache.longn;
					float latd=1.0f-(float)latc/(float)cache.latn;
					float longdd=(float)(longc+1)/(float)cache.longn;
					float latdd=1.0f-(float)(latc+1)/(float)cache.latn;

					tessellator.pos(cache.sunPos[longc][latc], info.deepDepth / 2.0f);
					tessellator.texture(longd, latd);
					tessellator.color(1.0f, 1.0f, 1.0f);
					tessellator.normal(cache.sunNormal[longc][latc]);
					tessellator.writeVertex();
					
					tessellator.pos(cache.sunPos[longcd][latc], info.deepDepth / 2.0f);
					tessellator.texture(longdd, latd);
					tessellator.color(1.0f, 1.0f, 1.0f);
					tessellator.normal(cache.sunNormal[longcd][latc]);
					tessellator.writeVertex();
					
					tessellator.pos(cache.sunPos[longcd][latc+1], info.deepDepth / 2.0f);
					tessellator.texture(longdd, latdd);
					tessellator.color(1.0f, 1.0f, 1.0f);
					tessellator.normal(cache.sunNormal[longcd][latc+1]);
					tessellator.writeVertex();
					
					tessellator.pos(cache.sunPos[longc][latc+1], info.deepDepth / 2.0f);
					tessellator.texture(longd, latdd);
					tessellator.color(1.0f, 1.0f, 1.0f);
					tessellator.normal(cache.sunNormal[longc][latc+1]);
					tessellator.writeVertex();
				}
			}
			
			tessellator.end();
		}
	}
}