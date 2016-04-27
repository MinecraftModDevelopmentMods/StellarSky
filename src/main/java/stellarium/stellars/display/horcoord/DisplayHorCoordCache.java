package stellarium.stellars.display.horcoord;

import stellarapi.api.ICelestialCoordinate;
import stellarapi.api.ISkyEffect;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Vector3;
import stellarapi.api.optics.IOpticalFilter;
import stellarapi.api.optics.IViewScope;
import stellarium.client.ClientSettings;
import stellarium.stellars.display.DisplaySettings;
import stellarium.stellars.display.IDisplayRenderCache;
import stellarium.util.math.VectorHelper;

public class DisplayHorCoordCache implements IDisplayRenderCache<DisplayHorCoord> {
	
	private Vector3 baseColor, heightColor, azimuthColor;
	protected Vector3[][] displayvec = null;
	protected Vector3[][] colorvec = null;
	protected int latn, longn;
	protected boolean enabled;
	protected float brightness;
	
	private int renderId;

	@Override
	public void initialize(ClientSettings settings, DisplaySettings specificSettings, DisplayHorCoord display) {
		this.latn = display.displayFrag;
		this.longn = 2*display.displayFrag;
		this.enabled = display.displayEnabled;
		if(this.enabled)
		{
			this.displayvec = VectorHelper.createAndInitialize(longn, latn+1);
			this.colorvec = VectorHelper.createAndInitialize(longn, latn+1);
		}
		this.brightness = (float) display.displayAlpha;
		this.baseColor = new Vector3(display.displayBaseColor);
		this.heightColor = new Vector3(display.displayHeightColor);
		this.azimuthColor = new Vector3(display.displayAzimuthColor);
		heightColor.sub(this.baseColor);
		azimuthColor.sub(this.baseColor);
	}

	@Override
	public void updateCache(ClientSettings settings, DisplaySettings specificSettings, DisplayHorCoord object,
			ICelestialCoordinate coordinate, ISkyEffect sky, IViewScope scope, IOpticalFilter filter) {
		if(!this.enabled)
			return;
		
		for(int longc=0; longc<longn; longc++){
			for(int latc=0; latc<=latn; latc++){
				displayvec[longc][latc].set(new SpCoord(-longc*360.0/longn, latc*180.0/latn - 90.0).getVec());
				displayvec[longc][latc].scale(50.0);
				
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

	@Override
	public int getRenderId() {
		return this.renderId;
	}

	@Override
	public void setRenderId(int id) {
		this.renderId = id;
	}

}
