package stellarium.stellars.display.horgrid;

import org.lwjgl.opengl.GL11;

import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Vector3;
import stellarium.client.ClientSettings;
import stellarium.render.EnumRenderPass;
import stellarium.stellars.display.IDisplayRenderCache;
import stellarium.stellars.layer.StellarCacheInfo;
import stellarium.util.math.VectorHelper;

public class HorGridCache implements IDisplayRenderCache<HorGridSettings> {
	
	private Vector3 baseColor, heightColor, azimuthColor;
	protected Vector3[][] displayvec = null;
	protected Vector3[] horizon = null;
	protected Vector3[][] colorvec = null;
	protected int latn, longn;
	protected boolean enabled;
	protected float brightness;
	
	private int renderId;

	@Override
	public void initialize(ClientSettings settings, HorGridSettings specificSettings) {
		this.latn = specificSettings.displayFrag;
		this.longn = 2*specificSettings.displayFrag;
		this.enabled = specificSettings.displayEnabled;
		if(this.enabled) {
			this.displayvec = VectorHelper.createAndInitialize(longn, latn+1);
			this.colorvec = VectorHelper.createAndInitialize(longn, latn+1);
			this.horizon = VectorHelper.createAndInitialize(longn);
		}
		this.brightness = (float) specificSettings.displayAlpha;
		this.baseColor = new Vector3(specificSettings.displayBaseColor);
		this.heightColor = new Vector3(specificSettings.displayHeightColor);
		this.azimuthColor = new Vector3(specificSettings.displayAzimuthColor);
		heightColor.sub(this.baseColor);
		azimuthColor.sub(this.baseColor);
	}

	@Override
	public void updateCache(ClientSettings settings, HorGridSettings specificSettings, StellarCacheInfo info) {
		if(!this.enabled)
			return;
		
		for(int longc=0; longc<longn; longc++){
			horizon[longc].set(new SpCoord(-longc*360.0/longn, 0.0).getVec());
			horizon[longc].scale(EnumRenderPass.getDeepDepth() + 1.0);
			
			for(int latc=0; latc<=latn; latc++){
				displayvec[longc][latc].set(new SpCoord(-longc*360.0/longn, latc*180.0/latn - 90.0).getVec());
				displayvec[longc][latc].scale(EnumRenderPass.getDeepDepth() + 2.0);
				
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
