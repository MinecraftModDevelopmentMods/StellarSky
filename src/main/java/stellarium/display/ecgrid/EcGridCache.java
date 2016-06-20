package stellarium.display.ecgrid;

import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Vector3;
import stellarium.client.ClientSettings;
import stellarium.display.DisplayCacheInfo;
import stellarium.display.IDisplayCache;
import stellarium.util.math.Allocator;

public class EcGridCache implements IDisplayCache<EcGridSettings> {

	private Vector3 baseColor, latitudeColor, longitudeColor;
	protected Vector3[][] displayvec = null;
	protected Vector3[][] colorvec = null;
	protected Vector3[] ecliptic = null;
	protected int latn, longn;
	protected boolean enabled, gridEnabled, eclipticEnabled;
	protected float brightness;

	@Override
	public void initialize(ClientSettings settings, EcGridSettings specificSettings) {
		this.latn = specificSettings.displayFrag;
		this.longn = 2*specificSettings.displayFrag;
		this.enabled = specificSettings.displayEnabled;
		this.eclipticEnabled = specificSettings.eclipticEnabled;
		this.gridEnabled = specificSettings.gridEnabled;
		if(this.enabled) {
			if(this.gridEnabled) {
				this.displayvec = Allocator.createAndInitialize(longn, latn+1);
				this.colorvec = Allocator.createAndInitialize(longn, latn+1);
			}
			if(this.eclipticEnabled)
				this.ecliptic = Allocator.createAndInitialize(longn);
		}
		this.brightness = (float) specificSettings.displayAlpha;
		this.baseColor = new Vector3(specificSettings.displayBaseColor);
		this.latitudeColor = new Vector3(specificSettings.displayHeightColor);
		this.longitudeColor = new Vector3(specificSettings.displayAzimuthColor);
		latitudeColor.sub(this.baseColor);
		longitudeColor.sub(this.baseColor);
	}

	@Override
	public void updateCache(DisplayCacheInfo info) {
		if(!this.enabled)
			return;

		Vector3 Buf = new Vector3();
		SpCoord coord;
		for(int longc=0; longc<longn; longc++){
			if(this.eclipticEnabled) {
				Buf.set(new SpCoord(-longc*360.0/longn, 0.0).getVec());
				info.projectionToGround.transform(Buf);

				coord = new SpCoord();
				coord.setWithVec(Buf);

				info.applyAtmRefraction(coord);

				ecliptic[longc].set(coord.getVec());
			}

			if(this.gridEnabled) {
				for(int latc=0; latc<=latn; latc++){
					Buf.set(new SpCoord(-longc*360.0/longn, latc*180.0/latn - 90.0).getVec());
					info.projectionToGround.transform(Buf);

					coord = new SpCoord();
					coord.setWithVec(Buf);

					info.applyAtmRefraction(coord);

					displayvec[longc][latc].set(coord.getVec());

					colorvec[longc][latc].set(this.baseColor);

					Buf.set(this.latitudeColor);
					Buf.scale((double)latc/latn);
					colorvec[longc][latc].add(Buf);

					Buf.set(this.longitudeColor);
					Buf.scale((double)longc/longn);
					colorvec[longc][latc].add(Buf);
				}
			}
		}
	}

}
