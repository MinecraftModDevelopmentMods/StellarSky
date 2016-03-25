package stellarium.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import sciapi.api.value.IValRef;
import sciapi.api.value.euclidian.CrossUtil;
import sciapi.api.value.euclidian.EVector;
import sciapi.api.value.euclidian.IEVector;
import sciapi.api.value.util.VOp;
import stellarium.client.ClientSettings;
import stellarium.stellars.StellarManager;
import stellarium.util.math.Spmath;
import stellarium.util.math.VecMath;

public class CelestialLayerSun implements ICelestialLayer {
	
	private static final ResourceLocation locationSunPng = new ResourceLocation("stellarium", "stellar/halo.png");
	
	private EVector pos = new EVector(3), dif = new EVector(3), dif2 = new EVector(3);

	@Override
	public void init(ClientSettings settings) { }
	
	@Override
	public void render(StellarManager manager, StellarRenderInfo info) {
		pos.set(manager.Sun.appPos);
		double size=manager.Sun.radius/Spmath.getD(VecMath.size(pos))*99.0*20;
		pos.set(VecMath.normalize(pos));
		dif.set(VOp.normalize(CrossUtil.cross((IEVector)pos, (IEVector)new EVector(0.0,0.0,1.0))));
		dif2.set((IValRef)CrossUtil.cross((IEVector)dif, (IEVector)pos));
		pos.set(VecMath.mult(99.0, pos));
		
		dif.set(VecMath.mult(size, dif));
		dif2.set(VecMath.mult(size, dif2));
		
		GlStateManager.color(1.0f, 1.0f, 1.0f, info.weathereff);
		info.mc.renderEngine.bindTexture(this.locationSunPng);
		info.worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		info.worldrenderer.pos(VecMath.getX(pos)+VecMath.getX(dif), VecMath.getY(pos)+VecMath.getY(dif), VecMath.getZ(pos)+VecMath.getZ(dif)).tex(0.0,0.0).endVertex();
		info.worldrenderer.pos(VecMath.getX(pos)+VecMath.getX(dif2), VecMath.getY(pos)+VecMath.getY(dif2), VecMath.getZ(pos)+VecMath.getZ(dif2)).tex(1.0,0.0).endVertex();
		info.worldrenderer.pos(VecMath.getX(pos)-VecMath.getX(dif), VecMath.getY(pos)-VecMath.getY(dif), VecMath.getZ(pos)-VecMath.getZ(dif)).tex(1.0,1.0).endVertex();
		info.worldrenderer.pos(VecMath.getX(pos)-VecMath.getX(dif2), VecMath.getY(pos)-VecMath.getY(dif2), VecMath.getZ(pos)-VecMath.getZ(dif2)).tex(0.0,1.0).endVertex();
		info.tessellator.draw();
	}
}
