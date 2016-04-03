package stellarium.stellars.milkyway;

import stellarium.stellars.layer.CelestialObject;
import stellarium.stellars.layer.IRenderCache;

public class Milkyway extends CelestialObject {

	public Milkyway(boolean isRemote) {
		super(isRemote);
	}

	@Override
	public IRenderCache generateCache() {
		return new MilkywayCache();
	}

	@Override
	public int getRenderId() {
		return LayerMilkyway.milkywayRenderId;
	}

}
