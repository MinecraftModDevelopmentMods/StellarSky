package stellarium.stellars.display.horcoord;

import org.lwjgl.opengl.GL11;

import stellarapi.api.lib.math.Spmath;
import stellarium.StellarSkyResources;
import stellarium.render.ICelestialObjectRenderer;
import stellarium.render.StellarRenderInfo;
import stellarium.stellars.Optics;

public class DisplayHorCoordRenderer implements ICelestialObjectRenderer<DisplayHorCoordCache> {

	@Override
	public void render(StellarRenderInfo info, DisplayHorCoordCache cache) {
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

				info.tessellator.setColorRGBA_F((float)cache.colorvec[longc][latc].x,
						(float)cache.colorvec[longc][latc].y,
						(float)cache.colorvec[longc][latc].z, cache.brightness);
				info.tessellator.addVertex(cache.displayvec[longc][latc].x, cache.displayvec[longc][latc].y, cache.displayvec[longc][latc].z);
				info.tessellator.addVertex(cache.displayvec[longc][latc+1].x, cache.displayvec[longc][latc+1].y, cache.displayvec[longc][latc+1].z);
				info.tessellator.addVertex(cache.displayvec[longcd][latc+1].x, cache.displayvec[longcd][latc+1].y, cache.displayvec[longcd][latc+1].z);
				info.tessellator.addVertex(cache.displayvec[longcd][latc].x, cache.displayvec[longcd][latc].y, cache.displayvec[longcd][latc].z);
			}
		}
		
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		info.tessellator.draw();
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

}
