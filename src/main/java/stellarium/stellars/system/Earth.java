package stellarium.stellars.system;

import stellarium.render.IRenderCache;

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
