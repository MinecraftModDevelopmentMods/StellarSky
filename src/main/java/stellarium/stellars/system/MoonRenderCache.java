package stellarium.stellars.system;

import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Vector3;
import stellarapi.api.optics.EnumRGBA;
import stellarapi.api.optics.EyeDetector;
import stellarium.client.ClientSettings;
import stellarium.stellars.layer.IRenderCache;
import stellarium.stellars.layer.StellarCacheInfo;
import stellarium.stellars.render.EnumRenderPass;
import stellarium.util.math.Allocator;

public class MoonRenderCache implements IRenderCache<Moon, SolarSystemClientSettings> {
	
	protected boolean shouldRenderGlow;
	
	private SpCoord appCoord, cache;
	protected Vector3 pos = new Vector3(), dif = new Vector3(), dif2 = new Vector3();
	protected int latn, longn;
	protected Vector3 moonPos[][], moonnormal[][];
	protected float moonilum[][];
	protected Vector3 buf = new Vector3();
	protected double size, difactor, appMag;
	protected float multiplier;
	protected double[] color = new double[4];

	@Override
	public void initialize(ClientSettings settings, SolarSystemClientSettings specificSettings, Moon moon) {
		this.appCoord = new SpCoord();
		this.latn = specificSettings.imgFrac;
		this.longn = 2*specificSettings.imgFrac;
		this.moonPos = Allocator.createAndInitialize(longn, latn+1);
		this.moonilum = new float[longn][latn+1];
		this.moonnormal = Allocator.createAndInitialize(longn, latn+1);
		this.cache = new SpCoord();
	}

	@Override
	public void updateCache(ClientSettings settings, SolarSystemClientSettings specificSettings, Moon object, StellarCacheInfo info) {
		buf.set(object.earthPos);
		info.projectionToGround.transform(this.buf);
		appCoord.setWithVec(this.buf);
		double airmass = info.calculateAirmass(this.appCoord);
		this.appMag = object.currentMag + airmass * info.getExtinctionRate(EnumRGBA.Alpha);
		info.applyAtmRefraction(this.appCoord);
		
		this.shouldRenderGlow = appCoord.y >= 0 || !info.hideObjectsUnderHorizon;
		
		this.size = object.radius/object.earthPos.size();
		this.difactor = 0.8 / 180.0 * Math.PI / this.size;
		this.difactor = this.difactor * this.difactor / Math.PI;
		
		if(this.shouldRenderGlow) {
			pos.set(appCoord.getVec());
			dif.set(new SpCoord(appCoord.x+90, 0.0).getVec());
			dif2.set(new SpCoord(appCoord.x, appCoord.y+90).getVec());

			pos.scale(EnumRenderPass.DEFAULT_OPAQUE_DEPTH);
			this.size *= (EnumRenderPass.DEFAULT_OPAQUE_DEPTH*5.0);
			
			dif.scale(this.size);
			dif2.scale(-this.size);
		}
		
		
		int latc, longc;
		for(longc=0; longc<longn; longc++){
			for(latc=0; latc<=latn; latc++){
				buf.set(object.posLocalM((double)longc/(double)longn*360.0, (double)latc/(double)latn*180.0-90.0));
				moonilum[longc][latc]=(float) (object.illumination(buf) * this.difactor * 1.5);
				moonnormal[longc][latc].set(object.posLocalM((double)longc/(double)longn*360.0, (double)latc/(double)latn*180.0-90.0));
				moonnormal[longc][latc].scale(-10000.0);
				buf.set(object.posLocalG(buf));
				info.projectionToGround.transform(buf);

				double size = buf.size();
				cache.setWithVec(buf);
				info.applyAtmRefraction(this.cache);

				moonPos[longc][latc].set(cache.getVec());
				moonPos[longc][latc].scale(size/object.earthPos.size()*EnumRenderPass.DEFAULT_OPAQUE_DEPTH);

				if(cache.y < 0 && info.hideObjectsUnderHorizon)
					moonilum[longc][latc]=0.0f;
			}
		}
		
		this.multiplier = (float)(info.lgp / (info.mp*info.mp));
		System.arraycopy(
				EyeDetector.getInstance().process(this.multiplier, info.filter, new double[] {1.0, 1.0, 1.0, object.brightness}),
				0, this.color, 0, color.length);
		color[3] /= object.brightness;
	}

	@Override
	public int getRenderId() {
		return LayerSolarSystem.moonRenderId;
	}

}
