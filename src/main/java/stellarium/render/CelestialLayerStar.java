package stellarium.render;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import sciapi.api.value.IValRef;
import sciapi.api.value.euclidian.CrossUtil;
import sciapi.api.value.euclidian.EVector;
import stellarium.client.ClientSettings;
import stellarium.stellars.Color;
import stellarium.stellars.Optics;
import stellarium.stellars.StellarManager;
import stellarium.stellars.background.BrStar;
import stellarium.util.math.Spmath;
import stellarium.util.math.VecMath;

public class CelestialLayerStar implements ICelestialLayer {
	
	private static final ResourceLocation locationStarPng = new ResourceLocation("stellarium", "stellar/star.png");
	
	private Random random;
	private ClientSettings settings;
	
	private EVector dif = new EVector(3), dif2 = new EVector(3);
	
	@Override
	public void init(ClientSettings settings) {
		this.settings = settings;
		this.random = new Random(System.currentTimeMillis());
	}

	@Override
	public void render(StellarManager manager, StellarRenderInfo info) {
		GL11.glEnable(GL11.GL_COLOR_MATERIAL);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

		info.mc.renderEngine.bindTexture(locationStarPng);

		EVector pos = new EVector(3);

		info.worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
		
		for(int i=0; i<BrStar.NumStar; i++){
			if(BrStar.stars[i].unable)
				continue;

			BrStar star=BrStar.stars[i];

			pos.set(VecMath.normalize(star.appPos));
			float Mag=star.App_Mag;
			float B_V=star.App_B_V;

			if(Mag > settings.mag_Limit)
				continue;

			if(info.mc.theWorld.provider.getDimension() == 0 && VecMath.getZ(pos)<0) continue;

			float size=0.5f;
			float alpha=Optics.getAlphaFromMagnitudeSparkling(Mag, info.bglight) - (((1-info.weathereff)/1)*20f);
			
			dif.set(CrossUtil.cross(pos, new EVector(0.0,0.0,1.0)));
			if(Spmath.getD(VecMath.size2(dif)) < 0.01)
				dif.set(CrossUtil.cross(pos, new EVector(0.0,1.0,0.0)));
			dif.set(VecMath.normalize(dif));
			dif2.set((IValRef)CrossUtil.cross(dif, pos));
			pos.set(VecMath.mult(100.0, pos));

			dif.set(VecMath.mult(size, dif));
			dif2.set(VecMath.mult(size, dif2));

			Color c=Color.getColor(B_V);
			
			int ialpha = (int)(info.weathereff*alpha*255.0);
			if(ialpha < 0)
				ialpha = 0;

			info.worldrenderer.pos(VecMath.getX(pos)+VecMath.getX(dif), VecMath.getY(pos)+VecMath.getY(dif), VecMath.getZ(pos)+VecMath.getZ(dif)).tex(0.0,0.0).color(c.r, c.g, c.b, ialpha).endVertex();
			info.worldrenderer.pos(VecMath.getX(pos)+VecMath.getX(dif2), VecMath.getY(pos)+VecMath.getY(dif2), VecMath.getZ(pos)+VecMath.getZ(dif2)).tex(1.0,0.0).color(c.r, c.g, c.b, ialpha).endVertex();
			info.worldrenderer.pos(VecMath.getX(pos)-VecMath.getX(dif), VecMath.getY(pos)-VecMath.getY(dif), VecMath.getZ(pos)-VecMath.getZ(dif)).tex(1.0,1.0).color(c.r, c.g, c.b, ialpha).endVertex();
			info.worldrenderer.pos(VecMath.getX(pos)-VecMath.getX(dif2), VecMath.getY(pos)-VecMath.getY(dif2), VecMath.getZ(pos)-VecMath.getZ(dif2)).tex(0.0,1.0).color(c.r, c.g, c.b, ialpha).endVertex();
		}

		info.tessellator.draw();
	}
	
}
