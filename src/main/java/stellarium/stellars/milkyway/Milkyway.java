package stellarium.stellars.milkyway;

import stellarium.render.IRenderCache;
import stellarium.stellars.layer.CelestialObject;

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
