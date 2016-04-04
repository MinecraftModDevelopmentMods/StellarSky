package stellarium.stellars.star;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import sciapi.api.value.IValRef;
import sciapi.api.value.euclidian.CrossUtil;
import sciapi.api.value.euclidian.EVector;
import stellarium.stellars.Optics;
import stellarium.stellars.layer.ICelestialObjectRenderer;
import stellarium.util.Color;
import stellarium.util.math.SpCoord;
import stellarium.util.math.Spmath;
import stellarium.util.math.VecMath;

public class StarRenderer implements ICelestialObjectRenderer<StarRenderCache> {
		
	@Override
	public void render(Minecraft mc, Tessellator tessellator, StarRenderCache cache, float bglight, float weathereff,
			float partialTicks) {
		if(!cache.shouldRender)
			return;
		
		EVector pos = cache.appCoord.getVec();
		float mag = cache.appMag;
		float B_V = cache.appB_V;

		float size=0.5f;
		float alpha=Optics.getAlphaFromMagnitudeSparkling(mag, bglight) - (((1-weathereff)/1)*20f);
		
		EVector dif = new SpCoord(cache.appCoord.x+90, 0.0).getVec();
		EVector dif2 = new SpCoord(cache.appCoord.x, cache.appCoord.y+90).getVec();

		pos.set(VecMath.mult(100.0, pos));
		dif.set(VecMath.mult(size, dif));
		dif2.set(VecMath.mult(-size, dif2));

		Color c = Color.getColor(B_V);
		
		tessellator.setColorRGBA(c.r, c.g, c.b, (int)(alpha*255.0));
		tessellator.addVertexWithUV(VecMath.getX(pos)+VecMath.getX(dif), VecMath.getY(pos)+VecMath.getY(dif), VecMath.getZ(pos)+VecMath.getZ(dif),0.0,0.0);
		tessellator.addVertexWithUV(VecMath.getX(pos)+VecMath.getX(dif2), VecMath.getY(pos)+VecMath.getY(dif2), VecMath.getZ(pos)+VecMath.getZ(dif2),1.0,0.0);
		tessellator.addVertexWithUV(VecMath.getX(pos)-VecMath.getX(dif), VecMath.getY(pos)-VecMath.getY(dif), VecMath.getZ(pos)-VecMath.getZ(dif),1.0,1.0);
		tessellator.addVertexWithUV(VecMath.getX(pos)-VecMath.getX(dif2), VecMath.getY(pos)-VecMath.getY(dif2), VecMath.getZ(pos)-VecMath.getZ(dif2),0.0,1.0);
	}

}
