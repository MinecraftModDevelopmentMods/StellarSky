package stellarium.stellars.milkyway;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import stellarium.render.StellarRenderInfo;
import stellarium.stellars.Optics;
import stellarium.stellars.layer.ICelestialObjectRenderer;
import stellarium.util.math.VecMath;

public class MilkywayRenderer implements ICelestialObjectRenderer<MilkywayCache> {
	
	private static final ResourceLocation locationMilkywayPng = new ResourceLocation("stellarium", "stellar/milkyway.png");

	@Override
	public void render(StellarRenderInfo info, MilkywayCache cache) {
		info.mc.renderEngine.bindTexture(locationMilkywayPng);
		
		info.worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

		float Mag = 3.5f;
		float alpha=Optics.getAlphaForGalaxy(Mag, info.bglight) - (((1-info.weathereff)/1)*20f);
		GlStateManager.color(1.0f, 1.0f, 1.0f, cache.brightness * alpha);

		for(int longc=0; longc<cache.longn; longc++){
			for(int latc=0; latc<cache.latn; latc++){
				int longcd=(longc+1)%cache.longn;
				double longd=1.0-(double)longc/(double)cache.longn;
				double latd=1.0-(double)latc/(double)cache.latn;
				double longdd=1.0-(double)(longc+1)/(double)cache.longn;
				double latdd=1.0-(double)(latc+1)/(double)cache.latn;

				info.worldrenderer.pos(VecMath.getX(cache.moonvec[longc][latc]), VecMath.getY(cache.moonvec[longc][latc]), VecMath.getZ(cache.moonvec[longc][latc])).tex(longd, latd).endVertex();
				info.worldrenderer.pos(VecMath.getX(cache.moonvec[longc][latc+1]), VecMath.getY(cache.moonvec[longc][latc+1]), VecMath.getZ(cache.moonvec[longc][latc+1])).tex(longd, latdd).endVertex();
				info.worldrenderer.pos(VecMath.getX(cache.moonvec[longcd][latc+1]), VecMath.getY(cache.moonvec[longcd][latc+1]), VecMath.getZ(cache.moonvec[longcd][latc+1])).tex(longdd, latdd).endVertex();
				info.worldrenderer.pos(VecMath.getX(cache.moonvec[longcd][latc]), VecMath.getY(cache.moonvec[longcd][latc]), VecMath.getZ(cache.moonvec[longcd][latc])).tex(longdd, latd).endVertex();
			}
		}
		
		info.tessellator.draw();
	}

}
