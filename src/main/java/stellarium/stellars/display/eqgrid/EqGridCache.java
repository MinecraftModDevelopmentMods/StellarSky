package stellarium.stellars.display.eqgrid;

import stellarapi.api.lib.math.Matrix3;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Vector3;
import stellarium.client.ClientSettings;
import stellarium.render.EnumRenderPass;
import stellarium.stellars.display.IDisplayRenderCache;
import stellarium.stellars.layer.StellarCacheInfo;
import stellarium.util.math.VectorHelper;

public class EqGridCache implements IDisplayRenderCache<EqGridSettings> {
	
	//Zero-time axial tilt
	public static final double e=0.4090926;
	public static final Matrix3 EqtoEc = new Matrix3();
	
	static {
		EqtoEc.setAsRotation(1.0, 0.0, 0.0, -e);
	}
	
	private Vector3 baseColor, decColor, RAColor;
	protected Vector3[][] displayvec = null;
	protected Vector3[][] colorvec = null;
	protected Vector3[] equator = null;
	protected int latn, longn;
	protected boolean enabled;
	protected float brightness;

	@Override
	public void initialize(ClientSettings settings, EqGridSettings specificSettings) {
		this.latn = specificSettings.displayFrag;
		this.longn = 2*specificSettings.displayFrag;
		this.enabled = specificSettings.displayEnabled;
		if(this.enabled) {
			this.displayvec = VectorHelper.createAndInitialize(longn, latn+1);
			this.colorvec = VectorHelper.createAndInitialize(longn, latn+1);
			this.equator = VectorHelper.createAndInitialize(longn);
		}
		this.brightness = (float) specificSettings.displayAlpha;
		this.baseColor = new Vector3(specificSettings.displayBaseColor);
		this.decColor = new Vector3(specificSettings.displayDecColor);
		this.RAColor = new Vector3(specificSettings.displayRAColor);
		decColor.sub(this.baseColor);
		RAColor.sub(this.baseColor);
	}

	@Override
	public void updateCache(ClientSettings settings, EqGridSettings specificSettings, StellarCacheInfo info) {
		if(!this.enabled)
			return;
		
		for(int longc=0; longc<longn; longc++){
			Vector3 Buf = new SpCoord(-longc*360.0/longn, 0.0).getVec();
			EqtoEc.transform(Buf);
			info.projectionToGround.transform(Buf);
			
			SpCoord coord = new SpCoord();
			coord.setWithVec(Buf);
			
			info.applyAtmRefraction(coord);
			
			equator[longc].set(coord.getVec());
			equator[longc].scale(EnumRenderPass.getDeepDepth() + 1.0);
			
			for(int latc=0; latc<=latn; latc++){
				Buf = new SpCoord(longc*360.0/longn, latc*180.0/latn - 90.0).getVec();
				EqtoEc.transform(Buf);
				info.projectionToGround.transform(Buf);
				
				coord = new SpCoord();
				coord.setWithVec(Buf);
				
				info.applyAtmRefraction(coord);

				displayvec[longc][latc].set(coord.getVec());
				displayvec[longc][latc].scale(EnumRenderPass.getDeepDepth() + 2.0);
				
				colorvec[longc][latc].set(this.baseColor);
				
				Buf.set(this.decColor);
				Buf.scale((double)latc/latn);
				colorvec[longc][latc].add(Buf);
				
				Buf.set(this.RAColor);
				Buf.scale((double)longc/longn);
				colorvec[longc][latc].add(Buf);
			}
		}
	}
}
