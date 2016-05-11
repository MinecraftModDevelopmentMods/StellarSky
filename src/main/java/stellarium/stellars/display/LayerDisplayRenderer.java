package stellarium.stellars.display;

import org.lwjgl.opengl.GL11;

import stellarium.render.EnumRenderPass;
import stellarium.render.ICelestialLayerRenderer;
import stellarium.render.StellarRenderInfo;

public class LayerDisplayRenderer implements ICelestialLayerRenderer {

	@Override
	public void preRender(StellarRenderInfo info) {
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	}

	@Override
	public void postRender(StellarRenderInfo info) { }

	@Override
	public boolean acceptPass(EnumRenderPass pass) {
		return pass == EnumRenderPass.OpaqueStellar || pass == EnumRenderPass.OpaqueSky;
	}

}
