package stellarium.stellars.system;

import stellarium.stellars.sketch.IRenderCache;

public class Earth extends Planet {

	public Earth(boolean isRemote, SolarObject parent) {
		super(isRemote, parent);
	}
	
	@Override
	public IRenderCache generateCache() {
		return null;
	}

	@Override
	public int getRenderId() {
		return -1;
	}

}
