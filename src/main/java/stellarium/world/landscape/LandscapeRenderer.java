package stellarium.world.landscape;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import stellarium.StellarSkyResources;
import stellarium.stellars.render.EnumRenderPass;
import stellarium.stellars.render.ICelestialObjectRenderer;
import stellarium.stellars.render.StellarRenderInfo;

@SideOnly(Side.CLIENT)
public class LandscapeRenderer {

	public void render(LandscapeRenderInfo info, LandscapeCache cache) {		
		info.mc.renderEngine.bindTexture(StellarSkyResources.resourceLandscape.getLocation());
		info.tessellator.startDrawingQuads();

		for(int longc=0; longc<cache.longn; longc++){
			for(int latc=0; latc<cache.latn; latc++){
				int longcd=(longc+1)%cache.longn;
				double longd=1.0-(double)longc/(double)cache.longn;
				double latd=1.0-(double)latc/(double)cache.latn;
				double longdd=1.0-(double)(longc+1)/(double)cache.longn;
				double latdd=1.0-(double)(latc+1)/(double)cache.latn;

				info.tessellator.addVertexWithUV(cache.displayvec[longc][latc].getX(), cache.displayvec[longc][latc].getY(), cache.displayvec[longc][latc].getZ(), longd, latd);
				info.tessellator.addVertexWithUV(cache.displayvec[longc][latc+1].getX(), cache.displayvec[longc][latc+1].getY(), cache.displayvec[longc][latc+1].getZ(), longd, latdd);
				info.tessellator.addVertexWithUV(cache.displayvec[longcd][latc+1].getX(), cache.displayvec[longcd][latc+1].getY(), cache.displayvec[longcd][latc+1].getZ(), longdd, latdd);
				info.tessellator.addVertexWithUV(cache.displayvec[longcd][latc].getX(), cache.displayvec[longcd][latc].getY(), cache.displayvec[longcd][latc].getZ(), longdd, latd);
			}
		}
		
		info.tessellator.draw();
	}

}
