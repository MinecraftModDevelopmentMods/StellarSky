package stellarium.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import sciapi.api.value.IValRef;
import sciapi.api.value.euclidian.CrossUtil;
import sciapi.api.value.euclidian.EVector;
import sciapi.api.value.euclidian.IEVector;
import stellarium.client.ClientSettings;
import stellarium.stellars.Optics;
import stellarium.stellars.StellarManager;
import stellarium.stellars.StellarObj;
import stellarium.util.math.Spmath;
import stellarium.util.math.VecMath;

public class CelestialLayerPlanet implements ICelestialLayer {
	
	private static final ResourceLocation locationStarPng = new ResourceLocation("stellarium", "stellar/star.png");
	
	private ClientSettings settings;

	private EVector dif = new EVector(3), dif2 = new EVector(3);
	
	@Override
	public void init(ClientSettings settings) {
		this.settings = settings;
	}
	
	@Override
	public void render(StellarManager manager, StellarRenderInfo info) {
		info.mc.renderEngine.bindTexture(locationStarPng);
		
		for(StellarObj object : manager.getPlanets()) {
			this.drawStellarObj(info, object);
		}
	}
	
	public void drawStellarObj(StellarRenderInfo info, StellarObj object) {
		this.drawStellarObj(info, object.appPos, object.appMag);
	}
	
	public void drawStellarObj(StellarRenderInfo info, EVector pos, double Mag) {

		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

		if(Mag > settings.mag_Limit) return;
		if(VecMath.getZ(pos)<0) return;

		float size=0.6f;
		float alpha=Optics.getAlphaFromMagnitude(Mag, info.bglight) - (((1-info.weathereff)/1)*20f);

		pos.set(VecMath.normalize(pos));
		
		dif.set(CrossUtil.cross((IEVector)pos, (IEVector)new EVector(0.0,0.0,1.0)));
		if(Spmath.getD(VecMath.size2(dif)) < 0.01)
			dif.set(CrossUtil.cross((IEVector)pos, (IEVector)new EVector(0.0,1.0,0.0)));
		dif.set(VecMath.normalize(dif));
		dif2.set((IValRef)CrossUtil.cross((IEVector)dif, (IEVector)pos));
		pos.set(VecMath.mult(99.0, pos));

		dif.set(VecMath.mult(size, dif));
		dif2.set(VecMath.mult(size, dif2));

		GlStateManager.color(1.0f, 1.0f, 1.0f, info.weathereff * alpha);
		info.worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		info.worldrenderer.pos(VecMath.getX(pos)+VecMath.getX(dif), VecMath.getY(pos)+VecMath.getY(dif), VecMath.getZ(pos)+VecMath.getZ(dif)).tex(0.0,0.0).endVertex();
		info.worldrenderer.pos(VecMath.getX(pos)+VecMath.getX(dif2), VecMath.getY(pos)+VecMath.getY(dif2), VecMath.getZ(pos)+VecMath.getZ(dif2)).tex(1.0,0.0).endVertex();
		info.worldrenderer.pos(VecMath.getX(pos)-VecMath.getX(dif), VecMath.getY(pos)-VecMath.getY(dif), VecMath.getZ(pos)-VecMath.getZ(dif)).tex(1.0,1.0).endVertex();
		info.worldrenderer.pos(VecMath.getX(pos)-VecMath.getX(dif2), VecMath.getY(pos)-VecMath.getY(dif2), VecMath.getZ(pos)-VecMath.getZ(dif2)).tex(0.0,1.0).endVertex();
		info.tessellator.draw();
	}

}
