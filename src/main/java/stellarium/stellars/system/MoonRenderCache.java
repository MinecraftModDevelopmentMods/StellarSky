package stellarium.stellars.system;

import stellarapi.api.ICelestialCoordinate;
import stellarapi.api.ISkyEffect;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Vector3;
import stellarapi.api.optics.FilterHelper;
import stellarapi.api.optics.IOpticalFilter;
import stellarapi.api.optics.IViewScope;
import stellarium.client.ClientSettings;
import stellarium.stellars.Optics;
import stellarium.stellars.layer.IRenderCache;
import stellarium.stellars.layer.StellarCacheInfo;
import stellarium.world.IStellarSkySet;

public class MoonRenderCache implements IRenderCache<Moon, SolarSystemClientSettings> {
	
	protected boolean shouldRenderGlow;
	
	protected SpCoord appCoord, cache;
	protected int latn, longn;
	protected Vector3 moonPos[][], moonnormal[][];
	protected float moonilum[][];
	protected Vector3 buf = new Vector3();
	protected double size, difactor, appMag;
	protected float multiplier;
	protected double[] color;

	@Override
	public void initialize(ClientSettings settings, SolarSystemClientSettings specificSettings, Moon moon) {
		this.appCoord = new SpCoord();
		this.latn = specificSettings.imgFrac;
		this.longn = 2*specificSettings.imgFrac;
		this.moonPos = new Vector3[longn][latn+1];
		this.moonilum = new float[longn][latn+1];
		this.moonnormal = new Vector3[longn][latn+1];
		this.cache = new SpCoord();
	}

	@Override
	public void updateCache(ClientSettings settings, SolarSystemClientSettings specificSettings, Moon object, StellarCacheInfo info) {
		Vector3 ref = new Vector3(object.earthPos);
		info.projectionToGround.transform(ref);
		appCoord.setWithVec(ref);
		double airmass = info.calculateAirmass(this.appCoord);
		this.appMag = object.currentMag + airmass * Optics.ext_coeff_V;
		info.applyAtmRefraction(this.appCoord);
		
		this.shouldRenderGlow = appCoord.y >= 0 || !info.hideObjectsUnderHorizon;
		
		this.size = object.radius/object.earthPos.size();
		this.difactor = 0.8 / 180.0 * Math.PI / this.size;
		this.difactor = this.difactor * this.difactor / Math.PI;
		
		this.size *= (98.0*5.0);
		
		int latc, longc;
		for(longc=0; longc<longn; longc++){
			for(latc=0; latc<=latn; latc++){
				buf.set(object.posLocalM((double)longc/(double)longn*360.0, (double)latc/(double)latn*180.0-90.0));
				moonilum[longc][latc]=(float) (object.illumination(buf) * this.difactor * 1.5);
				moonnormal[longc][latc] = new Vector3(buf);
				buf.set(object.posLocalG(buf));
				info.projectionToGround.transform(buf);

				cache.setWithVec(buf);
				info.applyAtmRefraction(this.cache);

				moonPos[longc][latc] = cache.getVec();
				moonPos[longc][latc].scale(98.0);

				if(cache.y < 0 && info.hideObjectsUnderHorizon)
					moonilum[longc][latc]=0.0f;
			}
		}
		
		this.multiplier = (float)(info.lgp / (info.mp*info.mp));
		this.color = FilterHelper.getFilteredRGBBounded(info.filter, new double[] {1.0, 1.0, 1.0});
	}

	@Override
	public int getRenderId() {
		return LayerSolarSystem.moonRenderId;
	}

}
