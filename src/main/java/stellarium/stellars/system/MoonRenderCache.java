package stellarium.stellars.system;

import sciapi.api.value.IValRef;
import sciapi.api.value.euclidian.EVector;
import stellarium.client.ClientSettings;
import stellarium.stellars.Optics;
import stellarium.stellars.layer.IRenderCache;
import stellarium.stellars.util.ExtinctionRefraction;
import stellarium.stellars.view.IStellarViewpoint;
import stellarium.util.math.SpCoord;
import stellarium.util.math.Spmath;
import stellarium.util.math.VecMath;

public class MoonRenderCache implements IRenderCache<Moon> {
	
	protected boolean shouldRenderGlow;
	
	protected SpCoord appCoord, cache;
	protected int latn, longn;
	protected EVector moonPos[][], moonnormal[][];
	protected float moonilum[][];
	protected EVector buf = new EVector(3);
	protected double size, difactor, appMag;

	@Override
	public void initialize(ClientSettings settings) {
		this.appCoord = new SpCoord();
		this.latn = settings.imgFrac;
		this.longn = 2*settings.imgFrac;
		this.moonPos = new EVector[longn][latn+1];
		this.moonilum = new float[longn][latn+1];
		this.moonnormal = new EVector[longn][latn+1];
		this.cache = new SpCoord();
	}

	@Override
	public void updateCache(ClientSettings settings, Moon object, IStellarViewpoint viewpoint) {
		EVector ref = new EVector(3);
		ref.set(viewpoint.getProjection().transform(object.earthPos));
		double airmass = viewpoint.getAirmass(ref, false);
		this.appMag = object.currentMag + airmass * Optics.ext_coeff_V;
		appCoord.setWithVec(ref);
		viewpoint.applyAtmRefraction(this.appCoord);
		
		this.shouldRenderGlow = appCoord.y >= 0 || !viewpoint.hideObjectsUnderHorizon();
		
		this.size = object.radius/Spmath.getD(VecMath.size(object.earthPos));
		this.difactor = 0.8 / 180.0 * Math.PI / this.size;
		this.difactor = this.difactor * this.difactor / Math.PI;
		
		this.size *= (98.0*5.0);
		
		int latc, longc;
		for(longc=0; longc<longn; longc++){
			for(latc=0; latc<=latn; latc++){
				buf.set(object.posLocalM((double)longc/(double)longn*360.0, (double)latc/(double)latn*180.0-90.0));
				moonilum[longc][latc]=(float) (object.illumination(buf) * this.difactor * 1.5);
				moonnormal[longc][latc] = new EVector(3).set(buf);
				buf.set(object.posLocalG(buf));
				IValRef ref2 = viewpoint.getProjection().transform(buf);

				cache.setWithVec(ref2);
				viewpoint.applyAtmRefraction(this.cache);

				moonPos[longc][latc] = VecMath.mult(98.0, cache.getVec()).getVal();

				if(cache.y < 0)
					moonilum[longc][latc]=0.0f;
			}
		}
	}

}
