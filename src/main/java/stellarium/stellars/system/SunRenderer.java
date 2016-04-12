package stellarium.stellars.system;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import sciapi.api.value.euclidian.EVector;
import stellarium.StellarSkyResources;
import stellarium.render.ICelestialObjectRenderer;
import stellarium.render.StellarRenderInfo;
import stellarium.util.math.SpCoord;
import stellarium.util.math.VecMath;

public class SunRenderer implements ICelestialObjectRenderer<SunRenderCache> {
	
	@Override
	public void render(StellarRenderInfo info, SunRenderCache cache) {
		EVector pos = cache.appCoord.getVec();
		EVector dif = new SpCoord(cache.appCoord.x+90, 0.0).getVec();
		EVector dif2 = new SpCoord(cache.appCoord.x, cache.appCoord.y+90).getVec();

		pos.set(VecMath.mult(99.0, pos));
		dif.set(VecMath.mult(cache.size, dif));
		dif2.set(VecMath.mult(-cache.size, dif2));
		
		GlStateManager.color(1.0f, 1.0f, 1.0f, info.weathereff);
		
		info.mc.renderEngine.bindTexture(StellarSkyResources.resourceSunHalo.getLocationFor(info.mc.theWorld));
		info.worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		info.worldrenderer.pos(VecMath.getX(pos)+VecMath.getX(dif), VecMath.getY(pos)+VecMath.getY(dif), VecMath.getZ(pos)+VecMath.getZ(dif)).tex(0.0,0.0).endVertex();
		info.worldrenderer.pos(VecMath.getX(pos)+VecMath.getX(dif2), VecMath.getY(pos)+VecMath.getY(dif2), VecMath.getZ(pos)+VecMath.getZ(dif2)).tex(1.0,0.0).endVertex();
		info.worldrenderer.pos(VecMath.getX(pos)-VecMath.getX(dif), VecMath.getY(pos)-VecMath.getY(dif), VecMath.getZ(pos)-VecMath.getZ(dif)).tex(1.0,1.0).endVertex();
		info.worldrenderer.pos(VecMath.getX(pos)-VecMath.getX(dif2), VecMath.getY(pos)-VecMath.getY(dif2), VecMath.getZ(pos)-VecMath.getZ(dif2)).tex(0.0,1.0).endVertex();

		info.tessellator.draw();
	}

}
