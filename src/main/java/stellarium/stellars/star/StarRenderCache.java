package stellarium.stellars.star;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import stellarapi.api.lib.config.IConfigHandler;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Vector3;
import stellarapi.api.optics.EnumRGBA;
import stellarapi.api.optics.EyeDetector;
import stellarapi.api.optics.Wavelength;
import stellarium.client.ClientSettings;
import stellarium.render.stellars.access.IStellarChecker;
import stellarium.render.stellars.layer.IObjRenderCache;
import stellarium.stellars.OpticsHelper;
import stellarium.stellars.render.ICelestialObjectRenderer;
import stellarium.stellars.util.StarColor;
import stellarium.util.math.StellarMath;
import stellarium.view.ViewerInfo;

public class StarRenderCache implements IObjRenderCache<BgStar, StarImage, IConfigHandler> {

	//private float magLimit;
	protected boolean shouldRender;
	protected SpCoord appPos = new SpCoord();
	protected float red, green, blue;

	@Override
	public void updateSettings(ClientSettings settings, IConfigHandler config, BgStar star) {
		
	}

	@Override
	public void updateCache(BgStar object, StarImage image, ViewerInfo info, IStellarChecker checker) {
		SpCoord appCoord = image.getCurrentHorizontalPos();
		this.appPos.x = appCoord.x;
		this.appPos.y = appCoord.y;

		double airmass = info.sky.calculateAirmass(this.appPos);

		/*this.appMag = (float) (object.mag + airmass * info.getExtinctionRate(EnumRGBA.Alpha)
			+ StellarMath.LumToMagWithoutSize(multiplier));*/

		this.shouldRender = true;
		/*if(this.appMag > this.magLimit) {
			this.shouldRender = false;
			return;
		}
		
		if(appCoord.y < 0.0 && info.hideObjectsUnderHorizon) {
			this.shouldRender = false;
			return;
		}*/

		StarColor starColor = StarColor.getColor(object.B_V);

		double alpha = OpticsHelper.getBrightnessFromMagnitude(object.mag + airmass * info.sky.getExtinctionRate(Wavelength.visible));
		this.red = (float) (alpha * starColor.r / 255.0 * StellarMath.MagToLumWithoutSize(airmass * info.sky.getExtinctionRate(Wavelength.red)));
		this.green = (float) (alpha * starColor.g / 255.0 * StellarMath.MagToLumWithoutSize(airmass * info.sky.getExtinctionRate(Wavelength.V)));
		this.blue = (float) (alpha * starColor.b / 255.0 * StellarMath.MagToLumWithoutSize(airmass * info.sky.getExtinctionRate(Wavelength.B)));

		checker.startDescription();
		checker.brightness(red, green, blue);
		this.shouldRender = checker.endCheckRendered();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ICelestialObjectRenderer getRenderer() {
		return StarRenderer.INSTANCE;
	}

}
