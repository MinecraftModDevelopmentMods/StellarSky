package stellarium.stellars.star;

import stellarapi.api.lib.math.Vector3;
import stellarium.render.celesital.ICelestialObjectRenderer;
import stellarium.render.celesital.StellarRenderInfo;
import stellarium.stellars.Optics;

public class StarRenderer implements ICelestialObjectRenderer<StarRenderCache> {
		
	@Override
	public void render(StellarRenderInfo info, StarRenderCache cache) {
		if(!cache.shouldRender)
			return;
		
		Vector3 pos = cache.pos;
		Vector3 dif = cache.dif;
		Vector3 dif2 = cache.dif2;
		float mag = cache.appMag;

		float alpha=Optics.getAlphaFromMagnitudeSparkling(mag, info.bglight) - (((1-info.weathereff)/1)*20f);
		
		info.tessellator.setColorRGBA_F((float)cache.color[0], (float)cache.color[1], (float)cache.color[2], alpha*(float)cache.color[3]);
		info.tessellator.addVertexWithUV(pos.getX()+dif.getX(), pos.getY()+dif.getY(), pos.getZ()+dif.getZ(),0.0,0.0);
		info.tessellator.addVertexWithUV(pos.getX()+dif2.getX(), pos.getY()+dif2.getY(), pos.getZ()+dif2.getZ(),1.0,0.0);
		info.tessellator.addVertexWithUV(pos.getX()-dif.getX(), pos.getY()-dif.getY(), pos.getZ()-dif.getZ(),1.0,1.0);
		info.tessellator.addVertexWithUV(pos.getX()-dif2.getX(), pos.getY()-dif2.getY(), pos.getZ()-dif2.getZ(),0.0,1.0);
	}

}
