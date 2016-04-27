package stellarium.stellars.star;

import stellarapi.api.lib.config.IConfigHandler;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Vector3;
import stellarapi.api.optics.FilterHelper;
import stellarapi.api.optics.Wavelength;
import stellarium.client.ClientSettings;
import stellarium.stellars.Optics;
import stellarium.stellars.layer.IRenderCache;
import stellarium.stellars.layer.StellarCacheInfo;
import stellarium.stellars.util.OpticalEffect;
import stellarium.stellars.util.StarColor;
import stellarium.util.math.StellarMath;
import stellarium.world.IStellarSkySet;

public class StarRenderCache implements IRenderCache<BgStar, IConfigHandler> {
	
	protected boolean shouldRender;
	protected SpCoord appCoord = new SpCoord();
	protected float appMag;
	protected float size;
	protected float multiplier;
	protected double[] color;
	
	@Override
	public void initialize(ClientSettings settings, IConfigHandler config, BgStar star) { }

	@Override
	public void updateCache(ClientSettings settings, IConfigHandler config, BgStar object, StellarCacheInfo info) {
		Vector3 ref = new Vector3(object.pos);
		info.projectionToGround.transform(ref);
		appCoord.setWithVec(ref);
		double airmass = info.calculateAirmass(this.appCoord);
		info.applyAtmRefraction(this.appCoord);
		
		this.size = (float) (info.resolution.get(Wavelength.visible) / 0.3);
		this.multiplier = (float)(info.lgp / (this.size * this.size * info.mp * info.mp));

		this.appMag = (float) (object.mag + airmass * info.extinctionRate.get(Wavelength.visible)
			+ StellarMath.LumToMagWithoutSize(this.multiplier));
		
		this.shouldRender = true;
		if(this.appMag > settings.mag_Limit)
		{
			this.shouldRender = false;
			return;
		}
		
		if(appCoord.y < 0.0 && info.hideObjectsUnderHorizon) {
			this.shouldRender = false;
			return;
		}
		
		StarColor starColor = StarColor.getColor(object.B_V);
		this.size *= 0.5f;
		this.color = FilterHelper.getFilteredRGB(info.filter,
					new double[] {
							starColor.r / 255.0 * StellarMath.MagToLumWithoutSize(airmass * info.extinctionRate.get(Wavelength.red)),
							starColor.g / 255.0 * StellarMath.MagToLumWithoutSize(airmass * info.extinctionRate.get(Wavelength.V)),
							starColor.b / 255.0 * StellarMath.MagToLumWithoutSize(airmass * info.extinctionRate.get(Wavelength.B))}
				);
		
		this.color = OpticalEffect.faintToWhite(this.color, Optics.getAlphaFromMagnitude(this.appMag, 0.0f));
	}

	@Override
	public int getRenderId() {
		return LayerBgStar.renderStarIndex;
	}

}
