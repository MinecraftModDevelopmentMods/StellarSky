package stellarium.stellars.system;

import stellarium.client.ClientSettings;
import stellarium.stellars.sketch.IRenderCache;
import stellarium.util.math.SpCoord;

public class PlanetRenderCache implements IRenderCache {
	
	protected boolean shouldRender;
	protected SpCoord appCoord;
	protected float appMag;
	
	@Override
	public void initialize(ClientSettings settings) {
		// TODO Auto-generated method stub
		
	}

}
