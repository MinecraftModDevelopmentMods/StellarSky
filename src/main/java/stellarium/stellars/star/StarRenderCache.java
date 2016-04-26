package stellarium.stellars.star;

import stellarapi.api.ICelestialCoordinate;
import stellarapi.api.ISkyEffect;
import stellarapi.api.lib.config.IConfigHandler;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Vector3;
import stellarapi.api.optics.FilterHelper;
import stellarapi.api.optics.IOpticalFilter;
import stellarapi.api.optics.IViewScope;
import stellarapi.api.optics.Wavelength;
import stellarium.client.ClientSettings;
import stellarium.stellars.Optics;
import stellarium.stellars.layer.IRenderCache;
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
	public void updateCache(ClientSettings settings, IConfigHandler config, BgStar object,
			ICelestialCoordinate coordinate, final ISkyEffect sky, IViewScope scope, IOpticalFilter filter) {
		Vector3 ref = new Vector3(object.pos);
		coordinate.getProjectionToGround().transform(ref);
		appCoord.setWithVec(ref);
		double airmass = sky.calculateAirmass(this.appCoord);
		sky.applyAtmRefraction(this.appCoord);
		
		this.size = (float) (scope.getResolution(Wavelength.visible) / 0.3);
		this.multiplier = (float)(scope.getLGP() / (this.size * this.size * scope.getMP() * scope.getMP()));

		this.appMag = (float) (object.mag + airmass * sky.getExtinctionRate(Wavelength.visible)
			+ StellarMath.LumToMagWithoutSize(this.multiplier));
		
		this.shouldRender = true;
		if(this.appMag > settings.mag_Limit)
		{
			this.shouldRender = false;
			return;
		}
		
		if(appCoord.y < 0.0
				&& sky instanceof IStellarSkySet
				&& ((IStellarSkySet) sky).hideObjectsUnderHorizon()) {
			this.shouldRender = false;
			return;
		}
		
		StarColor starColor = StarColor.getColor(object.B_V);
		this.size *= 0.5f;
		this.color = FilterHelper.getFilteredRGB(filter,
					new double[] {
							starColor.r / 255.0 * StellarMath.MagToLumWithoutSize(airmass * sky.getExtinctionRate(Wavelength.red)),
							starColor.g / 255.0 * StellarMath.MagToLumWithoutSize(airmass * sky.getExtinctionRate(Wavelength.V)),
							starColor.b / 255.0 * StellarMath.MagToLumWithoutSize(airmass * sky.getExtinctionRate(Wavelength.B))}
				);
		
		this.color = OpticalEffect.faintToWhite(this.color, Optics.getAlphaFromMagnitude(this.appMag, 0.0f));
	}

	@Override
	public int getRenderId() {
		return LayerBgStar.renderStarIndex;
	}

}
