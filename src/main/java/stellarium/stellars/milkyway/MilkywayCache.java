package stellarium.stellars.milkyway;

import sciapi.api.value.IValRef;
import sciapi.api.value.euclidian.EVector;
import stellarium.client.ClientSettings;
import stellarium.stellars.Optics;
import stellarium.stellars.layer.IRenderCache;
import stellarium.stellars.util.ExtinctionRefraction;
import stellarium.stellars.view.IStellarViewpoint;
import stellarium.util.math.Rotate;
import stellarium.util.math.SpCoord;
import stellarium.util.math.VecMath;

public class MilkywayCache implements IRenderCache<Milkyway> {
	
	//Zero-time axial tilt
	public static final double e=0.4090926;
	public static final Rotate EqtoEc = new Rotate('X').setRAngle(-e); 
	
	private EVector Buf = new EVector(3);
	protected EVector[][] moonvec = null;
	protected int latn, longn;
	protected float brightness;

	@Override
	public void initialize(ClientSettings settings) {
		this.latn = settings.imgFracMilkyway;
		this.longn = 2*settings.imgFracMilkyway;
		this.moonvec = new EVector[longn][latn+1];
		this.brightness = settings.milkywayBrightness;
	}

	@Override
	public void updateCache(ClientSettings settings, Milkyway object, IStellarViewpoint viewpoint) {
		for(int longc=0; longc<longn; longc++){
			for(int latc=0; latc<=latn; latc++){
				Buf.set(new SpCoord(longc*360.0/longn + 90.0, latc*180.0/latn - 90.0).getVec());
				Buf.set(VecMath.mult(50.0, Buf));
				IValRef ref = EqtoEc.transform(Buf);
				ref = viewpoint.getProjection().transform(ref);

				moonvec[longc][latc] = new EVector(3);
				moonvec[longc][latc].set(ExtinctionRefraction.refraction(ref, true));
			}
		}
	}

}
