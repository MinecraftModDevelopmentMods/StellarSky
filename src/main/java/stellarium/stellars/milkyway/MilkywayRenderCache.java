package stellarium.stellars.milkyway;

import stellarapi.api.ICelestialCoordinate;
import stellarapi.api.ISkyEffect;
import stellarapi.api.lib.math.Matrix3;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Vector3;
import stellarapi.api.optics.FilterHelper;
import stellarapi.api.optics.IOpticalFilter;
import stellarapi.api.optics.IViewScope;
import stellarium.client.ClientSettings;
import stellarium.stellars.layer.IRenderCache;

public class MilkywayRenderCache implements IRenderCache<Milkyway, MilkywaySettings> {
	
	//Zero-time axial tilt
	public static final double e=0.4090926;
	public static final Matrix3 EqtoEc = new Matrix3();
	
	static {
		EqtoEc.setAsRotation(1.0, 0.0, 0.0, -e);
	}
	
	private Vector3 Buf = new Vector3();
	protected Vector3[][] milkywayvec = null;
	protected int latn, longn;
	protected float brightness;
	protected double[] color;

	@Override
	public void initialize(ClientSettings settings, MilkywaySettings specificSettings, Milkyway dummy) {
		this.latn = specificSettings.imgFracMilkyway;
		this.longn = 2*specificSettings.imgFracMilkyway;
		this.milkywayvec = new Vector3[longn][latn+1];
	}

	@Override
	public void updateCache(ClientSettings settings, MilkywaySettings specificSettings, Milkyway object,
			ICelestialCoordinate coordinate, ISkyEffect sky, IViewScope scope, IOpticalFilter filter) {
		for(int longc=0; longc<longn; longc++){
			for(int latc=0; latc<=latn; latc++){
				Buf = new SpCoord(longc*360.0/longn + 90.0, latc*180.0/latn - 90.0).getVec();
				EqtoEc.transform(Buf);
				coordinate.getProjectionToGround().transform(Buf);
				
				SpCoord coord = new SpCoord();
				coord.setWithVec(Buf);
				
				sky.applyAtmRefraction(coord);

				milkywayvec[longc][latc] = coord.getVec();
				milkywayvec[longc][latc].scale(50.0);
			}
		}
		
		this.brightness = (float) (specificSettings.milkywayBrightness
				* scope.getLGP() / (scope.getMP() * scope.getMP()));
		this.color = FilterHelper.getFilteredRGBBounded(filter, new double[] {1.0, 1.0, 1.0});
	}

	@Override
	public int getRenderId() {
		return LayerMilkyway.milkywayRenderId;
	}

}
