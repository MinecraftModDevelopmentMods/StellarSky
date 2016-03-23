package stellarium.stellars.star;

import stellarium.client.ClientSettings;
import stellarium.stellars.sketch.IRenderCache;
import stellarium.util.math.SpCoord;

public class StarRenderCache implements IRenderCache {
	
	protected boolean shouldRender;
	protected SpCoord appCoord;
	protected float appMag;
	protected float appB_V;
	
	@Override
	public void initialize(ClientSettings settings) {
		// TODO Auto-generated method stub
		
	}

}
