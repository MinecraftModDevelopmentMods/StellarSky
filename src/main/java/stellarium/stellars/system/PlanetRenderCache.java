package stellarium.stellars.system;

import stellarapi.api.lib.config.IConfigHandler;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Vector3;
import stellarapi.api.optics.FilterHelper;
import stellarapi.api.optics.Wavelength;
import stellarium.client.ClientSettings;
import stellarium.stellars.Optics;
import stellarium.stellars.layer.IRenderCache;
import stellarium.stellars.layer.StellarCacheInfo;
import stellarium.world.IStellarSkySet;

public class PlanetRenderCache implements IRenderCache<Planet, IConfigHandler> {
	
	protected boolean shouldRender;
	protected SpCoord appCoord = new SpCoord();
	protected float appMag;
	protected float size;
	protected float multiplier;
	protected double[] color;
	
	@Override
	public void initialize(ClientSettings settings, IConfigHandler config, Planet planet) { }

	@Override
	public void updateCache(ClientSettings settings, IConfigHandler config, Planet object, StellarCacheInfo info) {
		Vector3 ref = new Vector3(object.earthPos);
		info.projectionToGround.transform(ref);
		appCoord.setWithVec(ref);
		double airmass = info.calculateAirmass(this.appCoord);
		info.applyAtmRefraction(this.appCoord);

		this.appMag = (float) (object.currentMag + airmass * Optics.ext_coeff_V);
		this.shouldRender = true;
		if(this.appMag > settings.mag_Limit)
		{
			this.shouldRender = false;
			return;
		}
		
		if(appCoord.y < 0.0 && info.hideObjectsUnderHorizon)
			this.shouldRender = false;
		
		this.size = (float) (info.resolution.get(Wavelength.visible) / 0.3);
		this.multiplier = (float)(info.lgp / (this.size * this.size * info.mp * info.mp));
		this.size *= 0.6f;
		this.color = FilterHelper.getFilteredRGBBounded(info.filter, new double[] {1.0, 1.0, 1.0});
	}

	@Override
	public int getRenderId() {
		return LayerSolarSystem.planetRenderId;
	}

}
