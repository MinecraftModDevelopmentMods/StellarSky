package stellarium.stellars.deepsky;

import stellarapi.api.lib.config.IConfigHandler;
import stellarium.client.ClientSettings;
import stellarium.render.stellars.access.IStellarChecker;
import stellarium.render.stellars.layer.IObjRenderCache;
import stellarium.stellars.layer.IRenderCache;
import stellarium.stellars.render.ICelestialObjectRenderer;
import stellarium.view.ViewerInfo;

public class DeepSkyObjectCache implements IObjRenderCache<DeepSkyObject, DeepSkyImage, IConfigHandler> {

	@Override
	public void updateSettings(ClientSettings settings, IConfigHandler specificSettings, DeepSkyObject object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateCache(DeepSkyObject object, DeepSkyImage image, ViewerInfo info, IStellarChecker checker) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ICelestialObjectRenderer getRenderer() {
		// TODO Auto-generated method stub
		return null;
	}


}
