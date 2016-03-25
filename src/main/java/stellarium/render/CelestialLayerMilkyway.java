package stellarium.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import sciapi.api.value.IValRef;
import sciapi.api.value.euclidian.EVector;
import stellarium.client.ClientSettings;
import stellarium.stellars.ExtinctionRefraction;
import stellarium.stellars.Optics;
import stellarium.stellars.StellarManager;
import stellarium.util.math.SpCoord;
import stellarium.util.math.VecMath;

public class CelestialLayerMilkyway implements ICelestialLayer {
	
	private static final ResourceLocation locationMilkywayPng = new ResourceLocation("stellarium", "stellar/milkyway.png");

	private EVector Buf = new EVector(3);
	private EVector[][] moonvec = null;
	private int latn, longn;
	private float brightness;
	
	@Override
	public void init(ClientSettings settings) {
		this.latn = settings.imgFracMilkyway;
		this.longn = 2*settings.imgFracMilkyway;
		this.moonvec = new EVector[longn][latn+1];
		this.brightness = settings.milkywayBrightness;
	}
	
	@Override
	public void render(StellarManager manager, StellarRenderInfo info) {
		for(int longc=0; longc<longn; longc++){
			for(int latc=0; latc<=latn; latc++){
				Buf.set(new SpCoord(longc*360.0/longn + 90.0, latc*180.0/latn - 90.0).getVec());
				Buf.set(VecMath.mult(120.0, Buf));
				IValRef ref = manager.transforms.EqtoEc.transform(Buf);
				ref = manager.transforms.projection.transform(ref);

				moonvec[longc][latc] = new EVector(3);
				moonvec[longc][latc].set(ExtinctionRefraction.refraction(ref, true));
			}
		}
				
		float Mag = 3.5f;
		float alpha=Optics.getAlphaForGalaxy(Mag, info.bglight) - (((1-info.weathereff)/1)*20f);
		GlStateManager.color(1.0f, 1.0f, 1.0f, this.brightness * alpha);
		
		info.mc.renderEngine.bindTexture(locationMilkywayPng);
		info.worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		
		for(int longc=0; longc<longn; longc++){
			for(int latc=0; latc<latn; latc++){
				int longcd=(longc+1)%longn;
				double longd=1.0-(double)longc/(double)longn;
				double latd=1.0-(double)latc/(double)latn;
				double longdd=1.0-(double)(longc+1)/(double)longn;
				double latdd=1.0-(double)(latc+1)/(double)latn;

				info.worldrenderer.pos(VecMath.getX(moonvec[longc][latc]), VecMath.getY(moonvec[longc][latc]), VecMath.getZ(moonvec[longc][latc])).tex(longd, latd).endVertex();
				info.worldrenderer.pos(VecMath.getX(moonvec[longc][latc+1]), VecMath.getY(moonvec[longc][latc+1]), VecMath.getZ(moonvec[longc][latc+1])).tex(longd, latdd).endVertex();
				info.worldrenderer.pos(VecMath.getX(moonvec[longcd][latc+1]), VecMath.getY(moonvec[longcd][latc+1]), VecMath.getZ(moonvec[longcd][latc+1])).tex(longdd, latdd).endVertex();
				info.worldrenderer.pos(VecMath.getX(moonvec[longcd][latc]), VecMath.getY(moonvec[longcd][latc]), VecMath.getZ(moonvec[longcd][latc])).tex(longdd, latd).endVertex();
			}
		}
		info.tessellator.draw();
	}

}
