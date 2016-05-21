package stellarium.stellars.system;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import stellarapi.api.lib.math.Vector3;
import stellarium.StellarSkyResources;
import stellarium.render.EnumRenderPass;
import stellarium.render.ICelestialObjectRenderer;
import stellarium.render.StellarRenderInfo;
import stellarium.stellars.Optics;
import stellarium.util.math.StellarMath;

public class PlanetRenderer implements ICelestialObjectRenderer<PlanetRenderCache> {

	@Override
	public void render(StellarRenderInfo info, PlanetRenderCache cache) {
		if(!cache.shouldRender || info.pass != EnumRenderPass.ShallowScattering)
			return;
		
		float mag = cache.appMag;

		float alpha = StellarMath.clip(Optics.getAlphaFromMagnitude(mag, info.bglight) * info.weathereff * (float)cache.color[3]);

		Vector3 pos = cache.pos;
		Vector3 dif = cache.dif;
		Vector3 dif2 = cache.dif2;
		
		info.mc.renderEngine.bindTexture(StellarSkyResources.resourcePlanetSmall.getLocation());
		
		GlStateManager.color((float)cache.color[0], (float)cache.color[1], (float)cache.color[2], alpha);
		
		info.worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		info.worldrenderer.pos(pos.getX()+dif.getX(), pos.getY()+dif.getY(), pos.getZ()+dif.getZ()).tex(0.0,0.0).endVertex();
		info.worldrenderer.pos(pos.getX()+dif2.getX(), pos.getY()+dif2.getY(), pos.getZ()+dif2.getZ()).tex(1.0,0.0).endVertex();
		info.worldrenderer.pos(pos.getX()-dif.getX(), pos.getY()-dif.getY(), pos.getZ()-dif.getZ()).tex(1.0,1.0).endVertex();
		info.worldrenderer.pos(pos.getX()-dif2.getX(), pos.getY()-dif2.getY(), pos.getZ()-dif2.getZ()).tex(0.0,1.0).endVertex();
		info.tessellator.draw();
	}

}
