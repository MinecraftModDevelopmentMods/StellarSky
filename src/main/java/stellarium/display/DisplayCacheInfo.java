package stellarium.display;

import stellarapi.api.lib.math.Matrix3;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.view.IAtmosphereEffect;
import stellarapi.api.view.ICCoordinates;

public class DisplayCacheInfo {

	public final Matrix3 projectionToGround;
	private final IAtmosphereEffect sky;

	public DisplayCacheInfo(ICCoordinates coordinate, IAtmosphereEffect sky) {
		this.projectionToGround = coordinate.getProjectionToGround();
		this.sky = sky;
	}
	
	public void applyAtmRefraction(SpCoord appCoord) {
		sky.applyAtmRefraction(appCoord);
	}

}
