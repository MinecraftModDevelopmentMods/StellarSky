package stellarium.stellars.star;

import javax.vecmath.Vector3d;

import stellarapi.api.lib.math.SpCoord;
import stellarium.render.ICelestialObjectRenderer;
import stellarium.render.StellarRenderInfo;
import stellarium.stellars.Optics;
import stellarium.util.Color;

public class StarRenderer implements ICelestialObjectRenderer<StarRenderCache> {
		
	@Override
	public void render(StellarRenderInfo info, StarRenderCache cache) {
		if(!cache.shouldRender)
			return;
		
		Vector3d pos = cache.appCoord.getVec();
		float mag = cache.appMag;
		float B_V = cache.appB_V;

		float alpha=Optics.getAlphaFromMagnitudeSparkling(mag, info.bglight) - (((1-info.weathereff)/1)*20f);
		
		Vector3d dif = new SpCoord(cache.appCoord.x+90, 0.0).getVec();
		Vector3d dif2 = new SpCoord(cache.appCoord.x, cache.appCoord.y+90).getVec();

		pos.scale(100.0);
		dif.scale(cache.size);
		dif2.scale(-cache.size);

		Color c = Color.getColor(B_V);
		
		info.tessellator.setColorRGBA(c.r, c.g, c.b, (int)(alpha*255.0));
		info.tessellator.addVertexWithUV(pos.x+dif.x, pos.y+dif.y, pos.z+dif.z,0.0,0.0);
		info.tessellator.addVertexWithUV(pos.x+dif2.x, pos.y+dif2.y, pos.z+dif2.z,1.0,0.0);
		info.tessellator.addVertexWithUV(pos.x-dif.x, pos.y-dif.y, pos.z-dif.z,1.0,1.0);
		info.tessellator.addVertexWithUV(pos.x-dif2.x, pos.y-dif2.y, pos.z-dif2.z,0.0,1.0);
	}

}
