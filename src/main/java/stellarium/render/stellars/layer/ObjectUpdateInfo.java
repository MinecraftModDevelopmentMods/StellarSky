package stellarium.render.stellars.layer;

import stellarium.render.stellars.access.IStellarChecker;
import stellarium.stellars.layer.IPerWorldImage;
import stellarium.view.ViewerInfo;

public class ObjectUpdateInfo {
	public final ViewerInfo viewer;
	public final IStellarChecker checker;
	private IPerWorldImage image;
	
	public ObjectUpdateInfo(ViewerInfo viewer, IStellarChecker checker) {
		this.viewer = viewer;
		this.checker = checker;
	}
	
	public void setup(IPerWorldImage image) {
		this.image = image;
	}

	public <T extends IPerWorldImage> T getWorldImage() {
		return (T) this.image;
	}

}