package stellarium.stellars.system;

import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Vector3;
import stellarium.StellarSkyResources;
import stellarium.render.ICelestialObjectRenderer;
import stellarium.render.StellarRenderInfo;
import stellarium.stellars.Optics;

public class PlanetRenderer implements ICelestialObjectRenderer<PlanetRenderCache> {

	@Override
	public void render(StellarRenderInfo info, PlanetRenderCache cache) {
		if(!cache.shouldRender)
			return;
		
		Vector3 pos = cache.appCoord.getVec();
		float mag = cache.appMag;

		float alpha=Optics.getAlphaFromMagnitude(mag, info.bglight) - (((1-info.weathereff)/1)*20f);
		
		Vector3 dif = new SpCoord(cache.appCoord.x+90, 0.0).getVec();
		Vector3 dif2 = new SpCoord(cache.appCoord.x, cache.appCoord.y+90).getVec();

		pos.scale(99.0);
		dif.scale(cache.size);
		dif2.scale(-cache.size);
		
		info.mc.renderEngine.bindTexture(StellarSkyResources.resourcePlanetSmall.getLocation());
		
		info.tessellator.startDrawingQuads();
		info.tessellator.setColorRGBA_F((float)cache.color[0], (float)cache.color[1], (float)cache.color[2], alpha*(float)cache.color[3]);
		info.tessellator.addVertexWithUV(pos.getX()+dif.getX(), pos.getY()+dif.getY(), pos.getZ()+dif.getZ(),0.0,0.0);
		info.tessellator.addVertexWithUV(pos.getX()+dif2.getX(), pos.getY()+dif2.getY(), pos.getZ()+dif2.getZ(),1.0,0.0);
		info.tessellator.addVertexWithUV(pos.getX()-dif.getX(), pos.getY()-dif.getY(), pos.getZ()-dif.getZ(),1.0,1.0);
		info.tessellator.addVertexWithUV(pos.getX()-dif2.getX(), pos.getY()-dif2.getY(), pos.getZ()-dif2.getZ(),0.0,1.0);
		info.tessellator.draw();
	}

}
