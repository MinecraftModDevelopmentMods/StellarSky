package stellarium.stellars.system;

import org.lwjgl.opengl.GL11;

import stellarium.render.celesital.EnumRenderPass;
import stellarium.render.celesital.ICelestialLayerRenderer;
import stellarium.render.celesital.StellarRenderInfo;

public class LayerSolarSystemRenderer implements ICelestialLayerRenderer {
	
	@Override
	public void preRender(StellarRenderInfo info) {
		GL11.glColor4f(1.0f, 1.0f, 1.0f, info.weathereff);
	}

	@Override
	public void postRender(StellarRenderInfo info) { }

	@Override
	public boolean acceptPass(EnumRenderPass pass) {
		return pass == EnumRenderPass.OpaqueStellar ||
				pass == EnumRenderPass.ShallowScattering;
	}

}
