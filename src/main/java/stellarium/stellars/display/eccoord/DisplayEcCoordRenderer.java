package stellarium.stellars.display.eccoord;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import stellarium.render.StellarRenderInfo;
import stellarium.stellars.display.IDisplayRenderer;

@SideOnly(Side.CLIENT)
public class DisplayEcCoordRenderer implements IDisplayRenderer<DisplayEcCoordCache> {

	@Override
	public void render(StellarRenderInfo info, DisplayEcCoordCache cache) {
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		
		if(!cache.enabled)
			return;
		
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		info.tessellator.startDrawingQuads();

		for(int longc=0; longc<cache.longn; longc++){
			for(int latc=0; latc<cache.latn; latc++){
				int longcd=(longc+1)%cache.longn;
				double longd=(double)longc/(double)cache.longn;
				double latd=(double)latc/(double)cache.latn;
				double longdd=(double)(longc+1)/(double)cache.longn;
				double latdd=(double)(latc+1)/(double)cache.latn;

				info.tessellator.setColorRGBA_F((float)cache.colorvec[longc][latc].getX(),
						(float)cache.colorvec[longc][latc].getY(),
						(float)cache.colorvec[longc][latc].getZ(), cache.brightness);
				info.tessellator.addVertex(cache.displayvec[longc][latc].getX(), cache.displayvec[longc][latc].getY(), cache.displayvec[longc][latc].getZ());
				info.tessellator.addVertex(cache.displayvec[longc][latc+1].getX(), cache.displayvec[longc][latc+1].getY(), cache.displayvec[longc][latc+1].getZ());
				info.tessellator.addVertex(cache.displayvec[longcd][latc+1].getX(), cache.displayvec[longcd][latc+1].getY(), cache.displayvec[longcd][latc+1].getZ());
				info.tessellator.addVertex(cache.displayvec[longcd][latc].getX(), cache.displayvec[longcd][latc].getY(), cache.displayvec[longcd][latc].getZ());
			}
		}
		
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		info.tessellator.draw();
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

}
