package stellarium.stellars.system;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import sciapi.api.value.euclidian.EVector;
import stellarium.stellars.Optics;
import stellarium.stellars.layer.ICelestialObjectRenderer;
import stellarium.stellars.layer.IRenderCache;
import stellarium.util.Color;
import stellarium.util.math.SpCoord;
import stellarium.util.math.VecMath;

public class PlanetRenderer implements ICelestialObjectRenderer<PlanetRenderCache> {

	private static final ResourceLocation locationStarPng = new ResourceLocation("stellarium", "stellar/star.png");
	
	@Override
	public void render(Minecraft mc, Tessellator tessellator, PlanetRenderCache cache, float bglight, float weathereff, float partialTicks) {
		if(!cache.shouldRender)
			return;
		
		EVector pos = cache.appCoord.getVec();
		float mag = cache.appMag;

		float size=0.6f;
		float alpha=Optics.getAlphaFromMagnitude(mag, bglight) - (((1-weathereff)/1)*20f);
		
		EVector dif = new SpCoord(cache.appCoord.x+90, 0.0).getVec();
		EVector dif2 = new SpCoord(cache.appCoord.x, cache.appCoord.y+90).getVec();

		pos.set(VecMath.mult(99.0, pos));
		dif.set(VecMath.mult(size, dif));
		dif2.set(VecMath.mult(-size, dif2));
		
		mc.renderEngine.bindTexture(this.locationStarPng);
		
		tessellator.startDrawingQuads();
		tessellator.setColorRGBA_F(1.0f, 1.0f, 1.0f, alpha);
		tessellator.addVertexWithUV(VecMath.getX(pos)+VecMath.getX(dif), VecMath.getY(pos)+VecMath.getY(dif), VecMath.getZ(pos)+VecMath.getZ(dif),0.0,0.0);
		tessellator.addVertexWithUV(VecMath.getX(pos)+VecMath.getX(dif2), VecMath.getY(pos)+VecMath.getY(dif2), VecMath.getZ(pos)+VecMath.getZ(dif2),1.0,0.0);
		tessellator.addVertexWithUV(VecMath.getX(pos)-VecMath.getX(dif), VecMath.getY(pos)-VecMath.getY(dif), VecMath.getZ(pos)-VecMath.getZ(dif),1.0,1.0);
		tessellator.addVertexWithUV(VecMath.getX(pos)-VecMath.getX(dif2), VecMath.getY(pos)-VecMath.getY(dif2), VecMath.getZ(pos)-VecMath.getZ(dif2),0.0,1.0);
		tessellator.draw();
	}

}
