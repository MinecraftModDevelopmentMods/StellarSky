package stellarium.stellars.star;

import javax.vecmath.Vector3d;

import stellarium.render.IRenderCache;
import stellarium.stellars.layer.CelestialObject;

public class BgStar extends CelestialObject {

	private static int renderIndex = -1;
	protected double mag, B_V;
	protected Vector3d pos;
	
	public BgStar(boolean isRemote, double mag, double B_V, Vector3d pos) {
		super(isRemote);
		this.mag = mag;
		this.B_V = B_V;
		this.pos = pos;
	}

	@Override
	public IRenderCache generateCache() {
		return new StarRenderCache();
	}
	
	public static void setRenderId(int id) {
		renderIndex = id;
	}

	@Override
	public int getRenderId() {
		return renderIndex;
	}

}
