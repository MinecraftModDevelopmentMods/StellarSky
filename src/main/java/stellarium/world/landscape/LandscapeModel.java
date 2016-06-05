package stellarium.world.landscape;

import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Vector3;
import stellarium.client.ClientSettings;
import stellarium.lib.hierarchy.Hierarchy;
import stellarium.lib.hierarchy.HierarchyCall;
import stellarium.util.math.Allocator;
import stellarium.world.StellarDimensionManager;

@Hierarchy
public class LandscapeModel {
	protected Vector3[][] displayvec = null;
	protected int latn, longn;
	protected boolean rendered;
	
	private int renderId;
	
	@HierarchyCall(id = "initializeClientSettings")
	public void initializeSettings(ClientSettings settings) {
		settings.putSubConfig(LandscapeClientSettings.KEY, new LandscapeClientSettings());
	}

	@HierarchyCall(id = "updateClientSettings")
	public void updateSettings(ClientSettings settings) {
		if(!this.rendered)
			return;

		LandscapeClientSettings layerSettings = (LandscapeClientSettings) settings.getSubConfig(LandscapeClientSettings.KEY);
		this.latn = layerSettings.displayFrag;
		this.longn = 2*layerSettings.displayFrag;
		this.displayvec = Allocator.createAndInitialize(longn, latn+1);
	}
	
	@HierarchyCall(id = "dimensionLoad")
	public void dimensionLoad(StellarDimensionManager dimManager) {
		this.rendered = dimManager.getSettings().isLandscapeEnabled();
	}

	public void updateCache() {
		if(!this.rendered)
			return;

		for(int longc=0; longc<longn; longc++){
			for(int latc=0; latc<=latn; latc++){
				displayvec[longc][latc].set(new SpCoord(longc*360.0/longn, latc*180.0/latn - 90.0).getVec());
			}
		}
	}
}
