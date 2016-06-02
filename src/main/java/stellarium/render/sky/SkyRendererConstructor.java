package stellarium.render.sky;

import stellarium.lib.render.RendererRegistry;

public class SkyRendererConstructor {
	
	public void constructRenderer() {
		RendererRegistry.INSTANCE.generateDistribution(modelClass);
		IDistributionBuilder<Pass, SkyRenderInformation> builder;
	}

}
