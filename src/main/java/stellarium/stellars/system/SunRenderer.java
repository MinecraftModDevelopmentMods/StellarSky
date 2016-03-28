package stellarium.stellars.system;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import sciapi.api.value.IValRef;
import sciapi.api.value.euclidian.CrossUtil;
import sciapi.api.value.euclidian.EVector;
import sciapi.api.value.euclidian.IEVector;
import sciapi.api.value.util.VOp;
import stellarium.stellars.sketch.ICelestialObjectRenderer;
import stellarium.util.math.SpCoord;
import stellarium.util.math.VecMath;

public class SunRenderer implements ICelestialObjectRenderer<SunRenderCache> {
	
	private static final ResourceLocation locationSunPng = new ResourceLocation("stellarium", "stellar/halo.png");

	@Override
	public void render(Minecraft mc, Tessellator tessellator, SunRenderCache cache, float bglight, float weathereff,
			float partialTicks) {
		EVector pos = cache.appCoord.getVec();
		EVector dif = new SpCoord(cache.appCoord.x+90, 0.0).getVec();
		EVector dif2 = new SpCoord(cache.appCoord.x, cache.appCoord.y+90).getVec();

		pos.set(VecMath.mult(99.0, pos));
		dif.set(VecMath.mult(cache.size, dif));
		dif2.set(VecMath.mult(cache.size, dif2));
		
		GL11.glColor4f(1.0f, 1.0f, 1.0f, weathereff);
		
		mc.renderEngine.bindTexture(this.locationSunPng);
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(VecMath.getX(pos)+VecMath.getX(dif), VecMath.getY(pos)+VecMath.getY(dif), VecMath.getZ(pos)+VecMath.getZ(dif),0.0,0.0);
		tessellator.addVertexWithUV(VecMath.getX(pos)+VecMath.getX(dif2), VecMath.getY(pos)+VecMath.getY(dif2), VecMath.getZ(pos)+VecMath.getZ(dif2),1.0,0.0);
		tessellator.addVertexWithUV(VecMath.getX(pos)-VecMath.getX(dif), VecMath.getY(pos)-VecMath.getY(dif), VecMath.getZ(pos)-VecMath.getZ(dif),1.0,1.0);
		tessellator.addVertexWithUV(VecMath.getX(pos)-VecMath.getX(dif2), VecMath.getY(pos)-VecMath.getY(dif2), VecMath.getZ(pos)-VecMath.getZ(dif2),0.0,1.0);
		tessellator.draw();
	}

}
