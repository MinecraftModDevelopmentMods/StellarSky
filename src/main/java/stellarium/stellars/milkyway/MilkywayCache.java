package stellarium.stellars.milkyway;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

import stellarapi.api.ICelestialCoordinate;
import stellarapi.api.ISkyEffect;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.optics.IViewScope;
import stellarium.client.ClientSettings;
import stellarium.stellars.layer.IRenderCache;
import stellarium.stellars.util.ExtinctionRefraction;
import stellarium.world.IStellarSkySet;

public class MilkywayCache implements IRenderCache<Milkyway, MilkywaySettings> {
	
	//Zero-time axial tilt
	public static final double e=0.4090926;
	public static final Matrix3d EqtoEc = new Matrix3d();
	
	static {
		EqtoEc.set(new AxisAngle4d(1.0, 0.0, 0.0, -e));
	}
	
	private Vector3d Buf = new Vector3d();
	protected Vector3d[][] moonvec = null;
	protected int latn, longn;
	protected float brightness;

	@Override
	public void initialize(ClientSettings settings, MilkywaySettings specificSettings) {
		this.latn = specificSettings.imgFracMilkyway;
		this.longn = 2*specificSettings.imgFracMilkyway;
		this.moonvec = new Vector3d[longn][latn+1];
	}

	@Override
	public void updateCache(ClientSettings settings, MilkywaySettings specificSettings, Milkyway object,
			ICelestialCoordinate coordinate, ISkyEffect sky, IViewScope scope) {
		for(int longc=0; longc<longn; longc++){
			for(int latc=0; latc<=latn; latc++){
				Buf = new SpCoord(longc*360.0/longn + 90.0, latc*180.0/latn - 90.0).getVec();
				EqtoEc.transform(Buf);
				coordinate.getProjectionToGround().transform(Buf);
				
				SpCoord coord = new SpCoord();
				coord.setWithVec(Buf);
				
				sky.applyAtmRefraction(coord);

				moonvec[longc][latc] = coord.getVec();
				moonvec[longc][latc].scale(50.0);
			}
		}
		
		this.brightness = (float) (specificSettings.milkywayBrightness
				* scope.getLGP() / (scope.getMP() * scope.getMP()));
	}

	@Override
	public int getRenderId() {
		return LayerMilkyway.milkywayRenderId;
	}

}
