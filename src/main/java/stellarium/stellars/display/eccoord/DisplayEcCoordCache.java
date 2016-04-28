package stellarium.stellars.display.eccoord;

import stellarapi.api.ICelestialCoordinate;
import stellarapi.api.ISkyEffect;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Vector3;
import stellarapi.api.optics.IOpticalFilter;
import stellarapi.api.optics.IViewScope;
import stellarium.client.ClientSettings;
import stellarium.stellars.display.DisplaySettings;
import stellarium.stellars.display.IDisplayRenderCache;
import stellarium.stellars.layer.StellarCacheInfo;
import stellarium.util.math.VectorHelper;

public class DisplayEcCoordCache implements IDisplayRenderCache<DisplayEcCoordSettings> {
	
	private Vector3 baseColor, latitudeColor, longitudeColor;
	protected Vector3[][] displayvec = null;
	protected Vector3[][] colorvec = null;
	protected int latn, longn;
	protected boolean enabled;
	protected float brightness;
	
	@Override
	public void initialize(ClientSettings settings, DisplayEcCoordSettings specificSettings) {
		this.latn = specificSettings.displayFrag;
		this.longn = 2*specificSettings.displayFrag;
		this.enabled = specificSettings.displayEnabled;
		if(this.enabled)
		{
			this.displayvec = VectorHelper.createAndInitialize(longn, latn+1);
			this.colorvec = VectorHelper.createAndInitialize(longn, latn+1);
		}
		this.brightness = (float) specificSettings.displayAlpha;
		this.baseColor = new Vector3(specificSettings.displayBaseColor);
		this.latitudeColor = new Vector3(specificSettings.displayHeightColor);
		this.longitudeColor = new Vector3(specificSettings.displayAzimuthColor);
		latitudeColor.sub(this.baseColor);
		longitudeColor.sub(this.baseColor);
	}

	@Override
	public void updateCache(ClientSettings settings, DisplayEcCoordSettings specificSettings, StellarCacheInfo info) {
		if(!this.enabled)
			return;
		
		for(int longc=0; longc<longn; longc++){
			for(int latc=0; latc<=latn; latc++){
				Vector3 Buf = new SpCoord(longc*360.0/longn, latc*180.0/latn - 90.0).getVec();
				info.projectionToGround.transform(Buf);
				
				SpCoord coord = new SpCoord();
				coord.setWithVec(Buf);
				
				info.applyAtmRefraction(coord);

				displayvec[longc][latc].set(coord.getVec());
				displayvec[longc][latc].scale(50.0);
				
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
