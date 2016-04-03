package stellarium.stellars.system;

import sciapi.api.value.euclidian.EVector;
import stellarium.client.ClientSettings;
import stellarium.stellars.Optics;
import stellarium.stellars.layer.CelestialObject;
import stellarium.stellars.layer.IRenderCache;
import stellarium.stellars.view.IStellarViewpoint;
import stellarium.util.math.SpCoord;

public class PlanetRenderCache implements IRenderCache<SolarObject> {
	
	protected boolean shouldRender;
	protected SpCoord appCoord;
	protected float appMag;
	
	@Override
	public void initialize(ClientSettings settings) { }

	@Override
	public void updateCache(ClientSettings settings, SolarObject object, IStellarViewpoint viewpoint) {
		EVector ref = new EVector(3);
		ref.set(viewpoint.getProjection().transform(object.earthPos));
		double airmass = viewpoint.getAirmass(ref, false);
		this.appMag = (float) (object.currentMag + airmass * Optics.ext_coeff_V);
		if(this.appMag > settings.mag_Limit)
		{
			this.shouldRender = false;
			return;
		}
		
		appCoord.setWithVec(ref);
		viewpoint.applyAtmRefraction(this.appCoord);
		
		if(appCoord.y < 0.0 && viewpoint.hideObjectsUnderHorizon())
			this.shouldRender = false;
	}

}
