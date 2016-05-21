package stellarium.stellars.star;

import stellarapi.api.lib.math.Vector3;
import stellarium.render.ICelestialObjectRenderer;
import stellarium.render.StellarRenderInfo;
import stellarium.stellars.Optics;
import stellarium.util.math.StellarMath;


public class StarRenderer implements ICelestialObjectRenderer<StarRenderCache> {
		
	@Override
	public void render(StellarRenderInfo info, StarRenderCache cache) {
		if(!cache.shouldRender)
			return;
		
		Vector3 pos = cache.pos;
		Vector3 dif = cache.dif;
		Vector3 dif2 = cache.dif2;
		float mag = cache.appMag;

		float alpha= StellarMath.clip(Optics.getAlphaFromMagnitudeSparkling(mag, info.bglight) * info.weathereff * (float)cache.color[3]);
		
		info.worldrenderer.pos(pos.getX()+dif.getX(), pos.getY()+dif.getY(), pos.getZ()+dif.getZ()).tex(0.0,0.0)
			.color((float)cache.color[0], (float)cache.color[1], (float)cache.color[2], alpha).endVertex();
		info.worldrenderer.pos(pos.getX()+dif2.getX(), pos.getY()+dif2.getY(), pos.getZ()+dif2.getZ()).tex(1.0,0.0)
			.color((float)cache.color[0], (float)cache.color[1], (float)cache.color[2], alpha).endVertex();
		info.worldrenderer.pos(pos.getX()-dif.getX(), pos.getY()-dif.getY(), pos.getZ()-dif.getZ()).tex(1.0,1.0)
			.color((float)cache.color[0], (float)cache.color[1], (float)cache.color[2], alpha).endVertex();
		info.worldrenderer.pos(pos.getX()-dif2.getX(), pos.getY()-dif2.getY(), pos.getZ()-dif2.getZ()).tex(0.0,1.0)
			.color((float)cache.color[0], (float)cache.color[1], (float)cache.color[2], alpha).endVertex();
	}

}
