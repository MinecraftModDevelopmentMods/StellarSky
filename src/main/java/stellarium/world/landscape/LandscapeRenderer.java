package stellarium.world.landscape;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import stellarium.StellarSkyResources;
import stellarium.client.ClientSettings;
import stellarium.lib.render.IGenericRenderer;
import stellarium.render.sky.SkyRenderInformation;

public class LandscapeRenderer implements IGenericRenderer<ClientSettings, Void, LandscapeModel, SkyRenderInformation> {
	@Override
	public void initialize(ClientSettings settings) { }

	@Override
	public void preRender(ClientSettings settings, SkyRenderInformation info) { }

	@Override
	public void renderPass(LandscapeModel model, Void pass, SkyRenderInformation info) {
		if(!model.rendered)
			return;
		
		info.minecraft.renderEngine.bindTexture(StellarSkyResources.resourceLandscape.getLocation());
		info.worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

		GL11.glPushMatrix();
		GL11.glScaled(info.deepDepth, info.deepDepth, info.deepDepth);

		for(int longc=0; longc<model.longn; longc++){
			for(int latc=0; latc<model.latn; latc++){
				int longcd=(longc+1)%model.longn;
				double longd=1.0-(double)longc/(double)model.longn;
				double latd=1.0-(double)latc/(double)model.latn;
				double longdd=1.0-(double)(longc+1)/(double)model.longn;
				double latdd=1.0-(double)(latc+1)/(double)model.latn;

				info.worldRenderer.pos(model.displayvec[longc][latc].getX(), model.displayvec[longc][latc].getY(), model.displayvec[longc][latc].getZ()).tex(longd, latd).endVertex();
				info.worldRenderer.pos(model.displayvec[longc][latc+1].getX(), model.displayvec[longc][latc+1].getY(), model.displayvec[longc][latc+1].getZ()).tex(longd, latdd).endVertex();
				info.worldRenderer.pos(model.displayvec[longcd][latc+1].getX(), model.displayvec[longcd][latc+1].getY(), model.displayvec[longcd][latc+1].getZ()).tex(longdd, latdd).endVertex();
				info.worldRenderer.pos(model.displayvec[longcd][latc].getX(), model.displayvec[longcd][latc].getY(), model.displayvec[longcd][latc].getZ()).tex(longdd, latd).endVertex();
			}
		}
		
		info.tessellator.draw();
		
		GL11.glPopMatrix();
	}

	@Override
	public void postRender(ClientSettings settings, SkyRenderInformation info) { }

}
