package stellarium.stellars.deepsky;

import stellarium.render.stellars.access.EnumStellarPass;
import stellarium.render.stellars.layer.LayerRHelper;
import stellarium.stellars.render.ICelestialLayerRenderer;

public enum DeepSkyRenderer implements ICelestialLayerRenderer {
	INSTANCE;

	@Override
	public void preRender(EnumStellarPass pass, LayerRHelper info) { }

	@Override
	public void postRender(EnumStellarPass pass, LayerRHelper info) { }

	@Override
	public boolean acceptPass(EnumStellarPass pass) {
		return pass == EnumStellarPass.Source;
	}

}
