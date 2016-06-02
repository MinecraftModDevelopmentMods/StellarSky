package stellarium.render.stellars.layer;

import stellarapi.api.lib.math.SpCoord;
import stellarium.render.stellars.AtmStellarUpdateInfo;
import stellarium.stellars.layer.IPerWorldImage;
import stellarium.view.ViewerInfo;

public class LayerUpdateInfo {
	public final SpCoord currentDirection;
	public final double currentRadius;
	public final ViewerInfo viewer;
	
	private ObjectUpdateInfo cachedObjectInfo;
	
	public LayerUpdateInfo(AtmStellarUpdateInfo update) {
		this.currentDirection = update.currentDirection;
		this.currentRadius = update.currentRadius;
		this.viewer = update.viewer;
		this.cachedObjectInfo = new ObjectUpdateInfo(update.viewer);
	}

	public ObjectUpdateInfo getInfoFor(IPerWorldImage image) {
		cachedObjectInfo.setup(image);
		return this.cachedObjectInfo;
	}
}
