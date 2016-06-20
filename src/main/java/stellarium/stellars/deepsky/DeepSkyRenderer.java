package stellarium.stellars.deepsky;

import stellarium.render.stellars.access.EnumStellarPass;
import stellarium.render.stellars.access.IStellarTessellator;
import stellarium.stellars.render.ICelestialLayerRenderer;

public enum DeepSkyRenderer implements ICelestialLayerRenderer {
	INSTANCE;

	@Override
	public void preRender(IStellarTessellator tessellator, EnumStellarPass pass) { }

	@Override
	public void postRender(IStellarTessellator tessellator, EnumStellarPass pass) { }

	@Override
	public boolean acceptPass(EnumStellarPass pass) {
		return pass == EnumStellarPass.SurfaceScatter;
	}

}
