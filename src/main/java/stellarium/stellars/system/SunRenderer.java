package stellarium.stellars.system;

import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Vector3;
import stellarium.StellarSkyResources;
import stellarium.render.EnumRenderPass;
import stellarium.render.ICelestialObjectRenderer;
import stellarium.render.StellarRenderInfo;

public class SunRenderer implements ICelestialObjectRenderer<SunRenderCache> {
	
	@Override
	public void render(StellarRenderInfo info, SunRenderCache cache) {
		if(info.pass != EnumRenderPass.ShallowScattering)
			return;
		
		Vector3 pos = cache.pos;
		Vector3 dif = cache.dif;
		Vector3 dif2 = cache.dif2;
		
		info.mc.renderEngine.bindTexture(StellarSkyResources.resourceSunHalo.getLocation());
		info.tessellator.startDrawingQuads();
		info.tessellator.addVertexWithUV(pos.getX()+dif.getX(),  pos.getY()+dif.getY(),  pos.getZ()+dif.getZ(), 0.0,0.0);
		info.tessellator.addVertexWithUV(pos.getX()+dif2.getX(), pos.getY()+dif2.getY(), pos.getZ()+dif2.getZ(),1.0,0.0);
		info.tessellator.addVertexWithUV(pos.getX()-dif.getX(),  pos.getY()-dif.getY(),  pos.getZ()-dif.getZ(), 1.0,1.0);
		info.tessellator.addVertexWithUV(pos.getX()-dif2.getX(), pos.getY()-dif2.getY(), pos.getZ()-dif2.getZ(),0.0,1.0);
		info.tessellator.draw();
	}

}
