package stellarium.stellars.system;

import javax.vecmath.Vector3d;

import stellarapi.api.ICelestialCoordinate;
import stellarapi.api.ISkyEffect;
import stellarapi.api.lib.config.IConfigHandler;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.optics.IViewScope;
import stellarium.client.ClientSettings;
import stellarium.stellars.layer.IRenderCache;
import stellarium.util.math.StellarMath;

public class SunRenderCache implements IRenderCache<Sun, IConfigHandler> {
	
	protected SpCoord appCoord = new SpCoord();
	protected double size;

	@Override
	public void initialize(ClientSettings settings, IConfigHandler config, Sun sun) { }

	@Override
	public void updateCache(ClientSettings settings, IConfigHandler config, Sun object,
			ICelestialCoordinate coordinate, ISkyEffect sky, IViewScope scope) {
		Vector3d ref = new Vector3d(object.earthPos);
		coordinate.getProjectionToGround().transform(ref);
		appCoord.setWithVec(ref);
		sky.applyAtmRefraction(this.appCoord);
		
		this.size = object.radius / object.earthPos.length()*99.0*20;
	}

	@Override
	public int getRenderId() {
		return LayerSolarSystem.sunRenderId;
	}

}
