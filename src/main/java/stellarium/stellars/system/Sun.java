package stellarium.stellars.system;

import javax.vecmath.Vector3d;

import stellarium.render.IRenderCache;

public class Sun extends SolarObject {

	public Sun(boolean isRemote) {
		super(isRemote);
		this.currentMag=-26.74;
	}

	@Override
	public Vector3d getRelativePos(double year) {
		return null;
	}
	
	protected void updateMagnitude(Vector3d earthFromSun) { }

	@Override
	public IRenderCache generateCache() {
		return new SunRenderCache();
	}

	@Override
	public int getRenderId() {
		return LayerSolarSystem.sunRenderId;
	}

}
