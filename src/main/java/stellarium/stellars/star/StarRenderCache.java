package stellarium.stellars.star;

import sciapi.api.value.euclidian.EVector;
import stellarium.client.ClientSettings;
import stellarium.stellars.Optics;
import stellarium.stellars.layer.CelestialObject;
import stellarium.stellars.layer.IRenderCache;
import stellarium.stellars.view.IStellarViewpoint;
import stellarium.util.math.SpCoord;

public class StarRenderCache implements IRenderCache<BgStar> {
	
	protected boolean shouldRender;
	protected SpCoord appCoord = new SpCoord();
	protected float appMag;
	protected float appB_V;
	
	@Override
	public void initialize(ClientSettings settings) { }

	@Override
	public void updateCache(ClientSettings settings, BgStar object, IStellarViewpoint viewpoint) {
		EVector ref = new EVector(3);
		ref.set(viewpoint.getProjection().transform(object.pos));
		double airmass = viewpoint.getAirmass(ref, false);
		this.appMag = (float) (object.mag + airmass * Optics.ext_coeff_V);
		if(this.appMag > settings.mag_Limit)
		{
			this.shouldRender = false;
			return;
		}
		this.appB_V = (float) (object.B_V + airmass * Optics.ext_coeff_B_V);
		
		appCoord.setWithVec(ref);
		viewpoint.applyAtmRefraction(this.appCoord);
		
		if(appCoord.y < 0.0 && viewpoint.hideObjectsUnderHorizon())
			this.shouldRender = false;
	}

}
