package stellarium.stellars.system;

import net.minecraft.util.ResourceLocation;
import sciapi.api.value.euclidian.EVector;
import stellarium.render.ICelestialObjectRenderer;
import stellarium.render.StellarRenderInfo;
import stellarium.stellars.Optics;
import stellarium.util.math.SpCoord;
import stellarium.util.math.VecMath;

public class PlanetRenderer implements ICelestialObjectRenderer<PlanetRenderCache> {

	private static final ResourceLocation locationStarPng = new ResourceLocation("stellarium", "stellar/star.png");
	
	@Override
	public void render(StellarRenderInfo info, PlanetRenderCache cache) {
		if(!cache.shouldRender)
			return;
		
		EVector pos = cache.appCoord.getVec();
		float mag = cache.appMag;

		float size=0.6f;
		float alpha=Optics.getAlphaFromMagnitude(mag, info.bglight) - (((1-info.weathereff)/1)*20f);
		
		EVector dif = new SpCoord(cache.appCoord.x+90, 0.0).getVec();
		EVector dif2 = new SpCoord(cache.appCoord.x, cache.appCoord.y+90).getVec();

		pos.set(VecMath.mult(99.0, pos));
		dif.set(VecMath.mult(size, dif));
		dif2.set(VecMath.mult(-size, dif2));
		
		info.mc.renderEngine.bindTexture(this.locationStarPng);
		
		info.tessellator.startDrawingQuads();
		info.tessellator.setColorRGBA_F(1.0f, 1.0f, 1.0f, alpha);
		info.tessellator.addVertexWithUV(VecMath.getX(pos)+VecMath.getX(dif), VecMath.getY(pos)+VecMath.getY(dif), VecMath.getZ(pos)+VecMath.getZ(dif),0.0,0.0);
		info.tessellator.addVertexWithUV(VecMath.getX(pos)+VecMath.getX(dif2), VecMath.getY(pos)+VecMath.getY(dif2), VecMath.getZ(pos)+VecMath.getZ(dif2),1.0,0.0);
		info.tessellator.addVertexWithUV(VecMath.getX(pos)-VecMath.getX(dif), VecMath.getY(pos)-VecMath.getY(dif), VecMath.getZ(pos)-VecMath.getZ(dif),1.0,1.0);
		info.tessellator.addVertexWithUV(VecMath.getX(pos)-VecMath.getX(dif2), VecMath.getY(pos)-VecMath.getY(dif2), VecMath.getZ(pos)-VecMath.getZ(dif2),0.0,1.0);
		info.tessellator.draw();
	}

}
