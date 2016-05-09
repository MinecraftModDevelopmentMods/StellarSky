package stellarium.stellars.milkyway;

import stellarapi.api.lib.math.Matrix3;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Vector3;
import stellarapi.api.optics.EyeDetector;
import stellarium.client.ClientSettings;
import stellarium.stellars.layer.IRenderCache;
import stellarium.stellars.layer.StellarCacheInfo;
import stellarium.util.math.VectorHelper;

public class MilkywayRenderCache implements IRenderCache<Milkyway, MilkywaySettings> {
	
	//Zero-time axial tilt
	public static final double e=0.4090926;
	public static final Matrix3 EqtoEc = new Matrix3();
	
	static {
		EqtoEc.setAsRotation(1.0, 0.0, 0.0, -e);
	}
	
	protected Vector3[][] milkywayvec = null;
	protected int latn, longn;
	protected double[] color = new double[4];

	@Override
	public void initialize(ClientSettings settings, MilkywaySettings specificSettings, Milkyway dummy) {
		this.latn = specificSettings.imgFracMilkyway;
		this.longn = 2*specificSettings.imgFracMilkyway;
		this.milkywayvec = VectorHelper.createAndInitialize(longn, latn+1);
	}

	@Override
	public void updateCache(ClientSettings settings, MilkywaySettings specificSettings, Milkyway object, StellarCacheInfo info) {
		for(int longc=0; longc<longn; longc++){
			for(int latc=0; latc<=latn; latc++){
				Vector3 Buf = new SpCoord(longc*360.0/longn + 90.0, latc*180.0/latn - 90.0).getVec();
				EqtoEc.transform(Buf);
				info.projectionToGround.transform(Buf);

				SpCoord coord = new SpCoord();
				coord.setWithVec(Buf);

				info.applyAtmRefraction(coord);

				milkywayvec[longc][latc].set(coord.getVec());
				milkywayvec[longc][latc].scale(50.0);
			}
		}
		
		double multiplier = info.lgp / (info.mp * info.mp);
		double[] result = EyeDetector.getInstance().process(multiplier, info.filter, new double[] {1.0, 1.0, 1.0, specificSettings.milkywayBrightness*0.0002});
		System.arraycopy(result, 0, this.color, 0, color.length);
		color[3]/=0.0002;
	}

	@Override
	public int getRenderId() {
		return LayerMilkyway.milkywayRenderId;
	}

}
