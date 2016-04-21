package stellarium.stellars.system;

import javax.vecmath.Vector3d;

import org.lwjgl.opengl.GL11;

import stellarapi.api.lib.math.SpCoord;
import stellarium.StellarSkyResources;
import stellarium.render.ICelestialObjectRenderer;
import stellarium.render.StellarRenderInfo;

public class SunRenderer implements ICelestialObjectRenderer<SunRenderCache> {
	
	@Override
	public void render(StellarRenderInfo info, SunRenderCache cache) {
		Vector3d pos = cache.appCoord.getVec();
		Vector3d dif = new SpCoord(cache.appCoord.x+90, 0.0).getVec();
		Vector3d dif2 = new SpCoord(cache.appCoord.x, cache.appCoord.y+90).getVec();

		pos.scale(99.0);
		dif.scale(cache.size);
		dif2.scale(-cache.size);
		
		info.mc.renderEngine.bindTexture(StellarSkyResources.resourceSunHalo.getLocation());
		info.tessellator.startDrawingQuads();
		info.tessellator.addVertexWithUV(pos.x+dif.x,  pos.y+dif.y,  pos.z+dif.z, 0.0,0.0);
		info.tessellator.addVertexWithUV(pos.x+dif2.x, pos.y+dif2.y, pos.z+dif2.z,1.0,0.0);
		info.tessellator.addVertexWithUV(pos.x-dif.x,  pos.y-dif.y,  pos.z-dif.z, 1.0,1.0);
		info.tessellator.addVertexWithUV(pos.x-dif2.x, pos.y-dif2.y, pos.z-dif2.z,0.0,1.0);
		info.tessellator.draw();
	}

}
