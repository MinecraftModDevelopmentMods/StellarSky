package stellarium.stellars.deepsky;

import stellarium.render.stellars.access.EnumStellarPass;
import stellarium.render.stellars.layer.LayerRI;
import stellarium.stellars.render.ICelestialLayerRenderer;

public enum DeepSkyRenderer implements ICelestialLayerRenderer {
	INSTANCE;

	@Override
	public void preRender(EnumStellarPass pass, LayerRI info) { }

	@Override
	public void postRender(EnumStellarPass pass, LayerRI info) { }

	@Override
	public boolean acceptPass(EnumStellarPass pass) {
		return pass == EnumStellarPass.SurfaceScatter;
	}

}
