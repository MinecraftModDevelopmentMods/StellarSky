package stellarium.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import sciapi.api.value.IValRef;
import sciapi.api.value.euclidian.CrossUtil;
import sciapi.api.value.euclidian.EVector;
import sciapi.api.value.euclidian.IEVector;
import sciapi.api.value.util.VOp;
import stellarium.StellarSky;
import stellarium.util.math.Spmath;
import stellarium.util.math.VecMath;

public class CelestialLayerSun implements ICelestialLayer {
	
	private static final ResourceLocation locationSunPng = new ResourceLocation("stellarium", "stellar/halo.png");
	
	private EVector pos = new EVector(3), dif = new EVector(3), dif2 = new EVector(3);

	@Override
	public void init(ClientSettings settings) { }
	
	@Override
	public void render(Minecraft mc, float bglight, float weathereff, double time) {
		pos.set(StellarSky.getManager().Sun.appPos);
		double size=StellarSky.getManager().Sun.radius/Spmath.getD(VecMath.size(pos))*99.0*20;
		pos.set(VecMath.normalize(pos));
		dif.set(VOp.normalize(CrossUtil.cross((IEVector)pos, (IEVector)new EVector(0.0,0.0,1.0))));
		dif2.set((IValRef)CrossUtil.cross((IEVector)dif, (IEVector)pos));
		pos.set(VecMath.mult(99.0, pos));

		dif.set(VecMath.mult(size, dif));
		dif2.set(VecMath.mult(size, dif2));

		Tessellator tessellator1 = Tessellator.instance;
		
		mc.renderEngine.bindTexture(this.locationSunPng);
		tessellator1.startDrawingQuads();
		tessellator1.addVertexWithUV(VecMath.getX(pos)+VecMath.getX(dif), VecMath.getY(pos)+VecMath.getY(dif), VecMath.getZ(pos)+VecMath.getZ(dif),0.0,0.0);
		tessellator1.addVertexWithUV(VecMath.getX(pos)+VecMath.getX(dif2), VecMath.getY(pos)+VecMath.getY(dif2), VecMath.getZ(pos)+VecMath.getZ(dif2),1.0,0.0);
		tessellator1.addVertexWithUV(VecMath.getX(pos)-VecMath.getX(dif), VecMath.getY(pos)-VecMath.getY(dif), VecMath.getZ(pos)-VecMath.getZ(dif),1.0,1.0);
		tessellator1.addVertexWithUV(VecMath.getX(pos)-VecMath.getX(dif2), VecMath.getY(pos)-VecMath.getY(dif2), VecMath.getZ(pos)-VecMath.getZ(dif2),0.0,1.0);
		tessellator1.draw();
	}
}
