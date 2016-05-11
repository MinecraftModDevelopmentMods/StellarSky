package stellarium.stellars.display.hordisplay;

import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Vector3;
import stellarium.client.ClientSettings;
import stellarium.stellars.display.IDisplayRenderCache;
import stellarium.stellars.layer.StellarCacheInfo;
import stellarium.util.math.VectorHelper;

public class HorOverlayCache implements IDisplayRenderCache<HorOverlaySettings> {
	
	protected Vector3[][] displayvec = null;
	protected int latn, longn;
	protected boolean enabled;
	
	private int renderId;

	@Override
	public void initialize(ClientSettings settings, HorOverlaySettings specificSettings) {
		this.latn = specificSettings.displayFrag;
		this.longn = 2*specificSettings.displayFrag;
		this.enabled = specificSettings.displayEnabled;
		if(this.enabled)
			this.displayvec = VectorHelper.createAndInitialize(longn, latn+1);
	}

	@Override
	public void updateCache(ClientSettings settings, HorOverlaySettings specificSettings, StellarCacheInfo info) {
		if(!this.enabled)
			return;
		
		for(int longc=0; longc<longn; longc++){
			for(int latc=0; latc<=latn; latc++){
				displayvec[longc][latc].set(new SpCoord(-longc*360.0/longn, latc*180.0/latn - 90.0).getVec());
				displayvec[longc][latc].scale(50.0);
			}
		}
	}

}
