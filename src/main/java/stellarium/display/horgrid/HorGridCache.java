package stellarium.display.horgrid;

import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Vector3;
import stellarium.client.ClientSettings;
import stellarium.display.DisplayCacheInfo;
import stellarium.display.IDisplayCache;
import stellarium.util.math.Allocator;

public class HorGridCache implements IDisplayCache<HorGridSettings> {
	
	private Vector3 baseColor, heightColor, azimuthColor;
	protected Vector3[][] displayvec = null;
	protected Vector3[] horizon = null;
	protected Vector3[][] colorvec = null;
	protected int latn, longn;
	protected boolean enabled, horizonEnabled, gridEnabled;
	protected float brightness;
	
	private int renderId;

	@Override
	public void initialize(ClientSettings settings, HorGridSettings specificSettings) {
		this.latn = specificSettings.displayFrag;
		this.longn = 2*specificSettings.displayFrag;
		this.enabled = specificSettings.displayEnabled;
		this.gridEnabled = specificSettings.gridEnabled;
		this.horizonEnabled = specificSettings.horizonEnabled;
		if(this.enabled) {
			if(this.gridEnabled) {
				this.displayvec = Allocator.createAndInitialize(longn, latn+1);
				this.colorvec = Allocator.createAndInitialize(longn, latn+1);
			}
			
			if(this.horizonEnabled)
				this.horizon = Allocator.createAndInitialize(longn);
		}
		this.brightness = (float) specificSettings.displayAlpha;
		this.baseColor = new Vector3(specificSettings.displayBaseColor);
		this.heightColor = new Vector3(specificSettings.displayHeightColor);
		this.azimuthColor = new Vector3(specificSettings.displayAzimuthColor);
		heightColor.sub(this.baseColor);
		azimuthColor.sub(this.baseColor);
	}

	@Override
	public void updateCache(DisplayCacheInfo info) {
		if(!this.enabled)
			return;
		
		for(int longc=0; longc<longn; longc++){
			if(this.horizonEnabled) {
				horizon[longc].set(new SpCoord(-longc*360.0/longn, 0.0).getVec());
			}
			
			if(this.gridEnabled) {
				for(int latc=0; latc<=latn; latc++){
					displayvec[longc][latc].set(new SpCoord(-longc*360.0/longn, latc*180.0/latn - 90.0).getVec());

					colorvec[longc][latc].set(this.baseColor);

					Vector3 Buf = new Vector3(this.heightColor);
					Buf.scale((double)latc/latn);
					colorvec[longc][latc].add(Buf);

					Buf.set(this.azimuthColor);
					Buf.scale((double)longc/longn);
					colorvec[longc][latc].add(Buf);
				}
			}
		}
	}

}
