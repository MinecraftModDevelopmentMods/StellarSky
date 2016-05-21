package stellarium.stellars.milkyway;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import stellarium.StellarSkyResources;
import stellarium.render.EnumRenderPass;
import stellarium.render.ICelestialObjectRenderer;
import stellarium.render.StellarRenderInfo;
import stellarium.stellars.Optics;

public class MilkywayRenderer implements ICelestialObjectRenderer<MilkywayRenderCache> {

	@Override
	public void render(StellarRenderInfo info, MilkywayRenderCache cache) {
		if(info.pass != EnumRenderPass.DeepScattering)
			return;
		
		info.mc.renderEngine.bindTexture(StellarSkyResources.resourceMilkyway.getLocation());
		
		float Mag = 3.5f;
		float alpha=Optics.getAlphaForGalaxy(Mag, info.bglight) * info.weathereff;
		
		GlStateManager.color((float)cache.color[0], (float)cache.color[1], (float)cache.color[2], (float)cache.color[3] * alpha);

		info.worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

		for(int longc=0; longc<cache.longn; longc++){
			for(int latc=0; latc<cache.latn; latc++){
				int longcd=(longc+1)%cache.longn;
				double longd=1.0-(double)longc/(double)cache.longn;
				double latd=1.0-(double)latc/(double)cache.latn;
				double longdd=1.0-(double)(longc+1)/(double)cache.longn;
				double latdd=1.0-(double)(latc+1)/(double)cache.latn;

				info.worldrenderer.pos(cache.milkywayvec[longc][latc].getX(), cache.milkywayvec[longc][latc].getY(), cache.milkywayvec[longc][latc].getZ()).tex(longd, latd).endVertex();
				info.worldrenderer.pos(cache.milkywayvec[longc][latc+1].getX(), cache.milkywayvec[longc][latc+1].getY(), cache.milkywayvec[longc][latc+1].getZ()).tex(longd, latdd).endVertex();
				info.worldrenderer.pos(cache.milkywayvec[longcd][latc+1].getX(), cache.milkywayvec[longcd][latc+1].getY(), cache.milkywayvec[longcd][latc+1].getZ()).tex(longdd, latdd).endVertex();
				info.worldrenderer.pos(cache.milkywayvec[longcd][latc].getX(), cache.milkywayvec[longcd][latc].getY(), cache.milkywayvec[longcd][latc].getZ()).tex(longdd, latd).endVertex();
			}
		}
		
		info.tessellator.draw();
	}

}
