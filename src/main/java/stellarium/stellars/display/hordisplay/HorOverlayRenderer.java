package stellarium.stellars.display.hordisplay;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import stellarium.render.EnumRenderPass;
import stellarium.render.StellarRenderInfo;
import stellarium.stellars.display.IDisplayRenderer;

@SideOnly(Side.CLIENT)
public class HorOverlayRenderer implements IDisplayRenderer<HorOverlayCache> {

	@Override
	public void render(StellarRenderInfo info, HorOverlayCache cache) {
		if(!cache.enabled || info.pass != EnumRenderPass.OpaqueSky)
			return;
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ZERO);
		
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		info.tessellator.startDrawingQuads();

		for(int longc=0; longc<cache.longn; longc++){
			for(int latc=0; latc<cache.latn; latc++){
				int longcd=(longc+1)%cache.longn;
				double longd=(double)longc/(double)cache.longn;
				double latd=(double)latc/(double)cache.latn;
				double longdd=(double)(longc+1)/(double)cache.longn;
				double latdd=(double)(latc+1)/(double)cache.latn;

				info.tessellator.addVertex(cache.displayvec[longc][latc].getX(), cache.displayvec[longc][latc].getY(), cache.displayvec[longc][latc].getZ());
				info.tessellator.addVertex(cache.displayvec[longcd][latc].getX(), cache.displayvec[longcd][latc].getY(), cache.displayvec[longcd][latc].getZ());
				info.tessellator.addVertex(cache.displayvec[longcd][latc+1].getX(), cache.displayvec[longcd][latc+1].getY(), cache.displayvec[longcd][latc+1].getZ());
				info.tessellator.addVertex(cache.displayvec[longc][latc+1].getX(), cache.displayvec[longc][latc+1].getY(), cache.displayvec[longc][latc+1].getZ());
			}
		}
		
		info.tessellator.draw();
		
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
	}

}
