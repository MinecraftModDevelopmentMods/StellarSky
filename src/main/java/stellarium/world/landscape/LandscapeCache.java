package stellarium.world.landscape;

import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Vector3;
import stellarium.client.ClientSettings;
import stellarium.render.EnumRenderPass;
import stellarium.stellars.layer.IRenderCache;
import stellarium.stellars.layer.StellarCacheInfo;
import stellarium.util.math.VectorHelper;

public class LandscapeCache {
	
	protected Vector3[][] displayvec = null;
	protected int latn, longn;
	
	private int renderId;

	public void initialize(LandscapeClientSettings specificSettings) {
		this.latn = specificSettings.displayFrag;
		this.longn = 2*specificSettings.displayFrag;
		this.displayvec = VectorHelper.createAndInitialize(longn, latn+1);
	}

	public void updateCache() {
		for(int longc=0; longc<longn; longc++){
			for(int latc=0; latc<=latn; latc++){
				displayvec[longc][latc].set(new SpCoord(longc*360.0/longn, latc*180.0/latn - 90.0).getVec());
				displayvec[longc][latc].scale(EnumRenderPass.getDeepDepth() * 0.7);
			}
		}
	}
}
