package stellarium.render.stellars.phased;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import stellarium.render.base.IRenderModel;
import stellarium.stellars.layer.CelestialManager;
import stellarium.stellars.layer.IRenderCache;
import stellarium.stellars.layer.StellarObjectContainer;

public class StellarRenderModel implements IRenderModel<StellarSettings, StellarUpdateInfo> {

	private CelestialManager celManager;
	//private Map<Obj, IRenderCache> renderCacheMap = Maps.newHashMap();
	
	@Override
	public void initialize(StellarSettings settings) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(StellarSettings settings, StellarUpdateInfo update) {
		// TODO Auto-generated method stub
		
	}

	public List<StellarObjectContainer> getLayers() {
		return celManager.getLayers();
	}

}
