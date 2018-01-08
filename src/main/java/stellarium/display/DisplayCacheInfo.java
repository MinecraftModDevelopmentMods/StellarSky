package stellarium.display;

import stellarapi.api.ICelestialCoordinates;
import stellarapi.api.ISkyEffect;
import stellarapi.api.lib.math.Matrix3;
import stellarapi.api.lib.math.SpCoord;

public class DisplayCacheInfo {

	public final Matrix3 projectionToGround;
	private final ISkyEffect sky;

	public DisplayCacheInfo(ICelestialCoordinates coordinate, ISkyEffect sky) {
		this.projectionToGround = coordinate.getProjectionToGround();
		this.sky = sky;
	}
	
	public void applyAtmRefraction(SpCoord appCoord) {
		sky.applyAtmRefraction(appCoord);
	}

}
