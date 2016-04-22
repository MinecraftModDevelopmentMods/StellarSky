package stellarium.stellars.display.horcoord;

import javax.vecmath.Vector3d;

import stellarapi.api.ICelestialCoordinate;
import stellarapi.api.ISkyEffect;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.optics.IViewScope;
import stellarium.client.ClientSettings;
import stellarium.stellars.display.DisplaySettings;
import stellarium.stellars.display.IDisplayRenderCache;

public class DisplayHorCoordCache implements IDisplayRenderCache<DisplayHorCoord> {
	
	private Vector3d Buf, heightColor, azimuthColor;
	protected Vector3d[][] displayvec = null;
	protected Vector3d[][] colorvec = null;
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
			this.displayvec = new Vector3d[longn][latn+1];
			this.colorvec = new Vector3d[longn][latn+1];
		}
		this.brightness = (float) display.displayAlpha;
		this.heightColor = new Vector3d(display.displayHeightColor);
		this.azimuthColor = new Vector3d(display.displayAzimuthColor);
	}

	@Override
	public void updateCache(ClientSettings settings, DisplaySettings specificSettings, DisplayHorCoord object,
			ICelestialCoordinate coordinate, ISkyEffect sky, IViewScope scope) {
		if(!this.enabled)
			return;
		
		for(int longc=0; longc<longn; longc++){
			for(int latc=0; latc<=latn; latc++){
				displayvec[longc][latc] =  new SpCoord(-longc*360.0/longn, latc*180.0/latn - 90.0).getVec();
				displayvec[longc][latc].scale(50.0);
				
				Buf = new Vector3d(this.heightColor);
				Buf.scale((double)latc/latn);
				colorvec[longc][latc] = new Vector3d(Buf);
				
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
