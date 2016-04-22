package stellarium.stellars.display.horcoord;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

import stellarapi.api.ICelestialCoordinate;
import stellarapi.api.ISkyEffect;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.optics.IViewScope;
import stellarium.client.ClientSettings;
import stellarium.stellars.display.DisplaySettings;
import stellarium.stellars.layer.IRenderCache;
import stellarium.stellars.util.ExtinctionRefraction;
import stellarium.world.IStellarSkySet;

public class DisplayHorCoordCache implements IRenderCache<DisplayHorCoord, DisplaySettings> {
	
	private Vector3d Buf, heightColor, azimuthColor;
	protected Vector3d[][] displayvec = null;
	protected Vector3d[][] colorvec = null;
	protected int latn, longn;
	protected boolean enabled;
	protected float brightness;
	
	private int renderId;

	public DisplayHorCoordCache(int renderId) {
		this.renderId = renderId;
	}

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
				Buf = new SpCoord(-longc*360.0/longn, latc*180.0/latn - 90.0).getVec();
				
				SpCoord coord = new SpCoord();
				coord.setWithVec(Buf);
				
				sky.applyAtmRefraction(coord);

				displayvec[longc][latc] = coord.getVec();
				displayvec[longc][latc].scale(50.0);
				
				Buf.set(this.heightColor);
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

}