package stellarium.stellars.system;

import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Vector3;
import stellarium.StellarSkyResources;
import stellarium.stellars.Optics;
import stellarium.stellars.render.EnumRenderPass;
import stellarium.stellars.render.ICelestialObjectRenderer;
import stellarium.stellars.render.StellarRenderInfo;

public class PlanetRenderer implements ICelestialObjectRenderer<PlanetRenderCache> {

	@Override
	public void render(StellarRenderInfo info, PlanetRenderCache cache) {
		if(!cache.shouldRender || info.pass != EnumRenderPass.ShallowScattering)
			return;
		
		float mag = cache.appMag;

		float alpha=Optics.getAlphaFromMagnitude(mag, info.bglight) - (((1-info.weathereff)/1)*20f);

		Vector3 pos = cache.pos;
		Vector3 dif = cache.dif;
		Vector3 dif2 = cache.dif2;
		
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
