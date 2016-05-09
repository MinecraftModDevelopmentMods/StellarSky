package stellarium.stellars.display;

import org.lwjgl.opengl.GL11;

import stellarium.render.ICelestialLayerRenderer;
import stellarium.render.StellarRenderInfo;

public class LayerDisplayRenderer implements ICelestialLayerRenderer {

	@Override
	public void preRender(StellarRenderInfo info) {
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		//GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ZERO);
	}

	@Override
	public void postRender(StellarRenderInfo info) {
		//GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
	}

}
