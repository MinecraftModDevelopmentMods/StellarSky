package stellarium.world.landscape;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stellarium.StellarSkyResources;

@SideOnly(Side.CLIENT)
public class LandscapeRenderer {

	public void render(LandscapeRenderInfo info, LandscapeCache cache) {		
		info.mc.renderEngine.bindTexture(StellarSkyResources.resourceLandscape.getLocation());
		info.worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

		for(int longc=0; longc<cache.longn; longc++){
			for(int latc=0; latc<cache.latn; latc++){
				int longcd=(longc+1)%cache.longn;
				double longd=1.0-(double)longc/(double)cache.longn;
				double latd=1.0-(double)latc/(double)cache.latn;
				double longdd=1.0-(double)(longc+1)/(double)cache.longn;
				double latdd=1.0-(double)(latc+1)/(double)cache.latn;

				info.worldRenderer.pos(cache.displayvec[longc][latc].getX(), cache.displayvec[longc][latc].getY(), cache.displayvec[longc][latc].getZ()).tex(longd, latd).endVertex();
				info.worldRenderer.pos(cache.displayvec[longc][latc+1].getX(), cache.displayvec[longc][latc+1].getY(), cache.displayvec[longc][latc+1].getZ()).tex(longd, latdd).endVertex();
				info.worldRenderer.pos(cache.displayvec[longcd][latc+1].getX(), cache.displayvec[longcd][latc+1].getY(), cache.displayvec[longcd][latc+1].getZ()).tex(longdd, latdd).endVertex();
				info.worldRenderer.pos(cache.displayvec[longcd][latc].getX(), cache.displayvec[longcd][latc].getY(), cache.displayvec[longcd][latc].getZ()).tex(longdd, latd).endVertex();
			}
		}
		
		info.tessellator.draw();
	}

}
