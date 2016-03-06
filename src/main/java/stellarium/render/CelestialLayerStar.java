package stellarium.render;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
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
	public void render(Minecraft mc, StellarManager manager, float bglight, float weathereff, double time) {
		GL11.glEnable(GL11.GL_COLOR_MATERIAL);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

		mc.renderEngine.bindTexture(locationStarPng);
		Tessellator tessellator1 = Tessellator.instance;

		EVector pos = new EVector(3);

		tessellator1.startDrawingQuads();
		for(int i=0; i<BrStar.NumStar; i++){
			if(BrStar.stars[i].unable)
				continue;

			BrStar star=BrStar.stars[i];

			pos.set(VecMath.normalize(star.appPos));
			float Mag=star.App_Mag;
			float B_V=star.App_B_V;

			if(Mag > settings.mag_Limit)
				continue;

			if(VecMath.getZ(pos)<0) continue;

			float size=0.5f;
			float alpha=Optics.getAlphaFromMagnitudeSparkling(Mag, bglight) - (((1-weathereff)/1)*20f);
			
			dif.set(CrossUtil.cross(pos, new EVector(0.0,0.0,1.0)));
			if(Spmath.getD(VecMath.size2(dif)) < 0.01)
				dif.set(CrossUtil.cross(pos, new EVector(0.0,1.0,0.0)));
			dif.set(VecMath.normalize(dif));
			dif2.set((IValRef)CrossUtil.cross(dif, pos));
			pos.set(VecMath.mult(100.0, pos));

			dif.set(VecMath.mult(size, dif));
			dif2.set(VecMath.mult(size, dif2));

			Color c=Color.getColor(B_V);

			tessellator1.setColorRGBA(c.r, c.g, c.b, (int)(alpha*255.0));
			tessellator1.addVertexWithUV(VecMath.getX(pos)+VecMath.getX(dif), VecMath.getY(pos)+VecMath.getY(dif), VecMath.getZ(pos)+VecMath.getZ(dif),0.0,0.0);
			tessellator1.addVertexWithUV(VecMath.getX(pos)+VecMath.getX(dif2), VecMath.getY(pos)+VecMath.getY(dif2), VecMath.getZ(pos)+VecMath.getZ(dif2),1.0,0.0);
			tessellator1.addVertexWithUV(VecMath.getX(pos)-VecMath.getX(dif), VecMath.getY(pos)-VecMath.getY(dif), VecMath.getZ(pos)-VecMath.getZ(dif),1.0,1.0);
			tessellator1.addVertexWithUV(VecMath.getX(pos)-VecMath.getX(dif2), VecMath.getY(pos)-VecMath.getY(dif2), VecMath.getZ(pos)-VecMath.getZ(dif2),0.0,1.0);
		}

		tessellator1.draw();
	}
	
}
