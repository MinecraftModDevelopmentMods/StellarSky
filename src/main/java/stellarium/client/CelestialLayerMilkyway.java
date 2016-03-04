package stellarium.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import sciapi.api.value.IValRef;
import sciapi.api.value.euclidian.EVector;
import stellarium.stellars.ExtinctionRefraction;
import stellarium.stellars.Optics;
import stellarium.util.math.SpCoord;
import stellarium.util.math.Transforms;
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
	public void render(Minecraft mc, float bglight, float weathereff, double time) {		
		for(int longc=0; longc<longn; longc++){
			for(int latc=0; latc<=latn; latc++){
				Buf.set(new SpCoord(longc*360.0/longn + 90.0, latc*180.0/latn - 90.0).getVec());
				Buf.set(VecMath.mult(50.0, Buf));
				IValRef ref=Transforms.EqtoEc.transform(Buf);
				ref=Transforms.projection.transform(ref);

				moonvec[longc][latc] = new EVector(3);
				moonvec[longc][latc].set(ExtinctionRefraction.refraction(ref, true));
			}
		}
		
		Tessellator tessellator1 = Tessellator.instance;
		
		mc.renderEngine.bindTexture(locationMilkywayPng);
		tessellator1.startDrawingQuads();
		
		float Mag = 3.5f;
		float alpha=Optics.getAlphaForGalaxy(Mag, bglight) - (((1-weathereff)/1)*20f);
		
		for(int longc=0; longc<longn; longc++){
			for(int latc=0; latc<latn; latc++){
				int longcd=(longc+1)%longn;
				double longd=1.0-(double)longc/(double)longn;
				double latd=1.0-(double)latc/(double)latn;
				double longdd=1.0-(double)(longc+1)/(double)longn;
				double latdd=1.0-(double)(latc+1)/(double)latn;

				tessellator1.setColorRGBA_F(1.0f, 1.0f, 1.0f, this.brightness * alpha);
				tessellator1.addVertexWithUV(VecMath.getX(moonvec[longc][latc]), VecMath.getY(moonvec[longc][latc]), VecMath.getZ(moonvec[longc][latc]), longd, latd);
				tessellator1.addVertexWithUV(VecMath.getX(moonvec[longc][latc+1]), VecMath.getY(moonvec[longc][latc+1]), VecMath.getZ(moonvec[longc][latc+1]), longd, latdd);
				tessellator1.addVertexWithUV(VecMath.getX(moonvec[longcd][latc+1]), VecMath.getY(moonvec[longcd][latc+1]), VecMath.getZ(moonvec[longcd][latc+1]), longdd, latdd);
				tessellator1.addVertexWithUV(VecMath.getX(moonvec[longcd][latc]), VecMath.getY(moonvec[longcd][latc]), VecMath.getZ(moonvec[longcd][latc]), longdd, latd);
			}
		}
		tessellator1.draw();
	}

}
