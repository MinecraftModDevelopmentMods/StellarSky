package stellarium.stellars.system;

import stellarapi.api.lib.config.IConfigHandler;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Vector3;
import stellarapi.api.optics.EnumRGBA;
import stellarapi.api.optics.EyeDetector;
import stellarium.client.ClientSettings;
import stellarium.stellars.Optics;
import stellarium.stellars.layer.IRenderCache;
import stellarium.stellars.layer.StellarCacheInfo;
import stellarium.stellars.render.EnumRenderPass;

public class PlanetRenderCache implements IRenderCache<Planet, IConfigHandler> {
	
	protected boolean shouldRender;
	protected SpCoord appCoord = new SpCoord();
	protected Vector3 pos = new Vector3(), dif = new Vector3(), dif2 = new Vector3();
	protected float appMag;
	protected float multiplier;
	protected double[] color = new double[4];
	
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
		
		pos.set(appCoord.getVec());
		dif.set(new SpCoord(appCoord.x+90, 0.0).getVec());
		dif2.set(new SpCoord(appCoord.x, appCoord.y+90).getVec());
		
		double size = (info.getResolution(EnumRGBA.Alpha) / 0.3);
		this.multiplier = (float)(info.lgp / (size * size * info.mp * info.mp));
		size *= 0.6f;
		System.arraycopy(
				EyeDetector.getInstance().process(this.multiplier, info.filter, new double[] {1.0, 1.0, 1.0, 0.1}),
				0, this.color, 0, color.length);
		
		pos.set(appCoord.getVec());
		appCoord.y += 90.0;
		dif2.set(appCoord.getVec());
		appCoord.x += 90.0;
		appCoord.y = 0.0;
		dif.set(appCoord.getVec());
		
		pos.scale(EnumRenderPass.DEFAULT_OPAQUE_DEPTH);
		dif.scale(size);
		dif2.scale(-size);
	}

	@Override
	public int getRenderId() {
		return LayerSolarSystem.planetRenderId;
	}

}
