package stellarium.stellars.system;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import stellarapi.api.lib.config.IConfigHandler;
import stellarapi.api.lib.math.SpCoord;
import stellarium.client.ClientSettings;
import stellarium.render.stellars.access.IStellarChecker;
import stellarium.render.stellars.layer.IObjRenderCache;
import stellarium.stellars.OpticsHelper;
import stellarium.stellars.render.ICelestialObjectRenderer;
import stellarium.view.ViewerInfo;

public class PlanetRenderCache implements IObjRenderCache<Planet, PlanetImage, IConfigHandler> {
	
	protected boolean shouldRender;
	protected SpCoord appCoord = new SpCoord();
	protected float brightness;
	protected float size;

	@Override
	public void updateSettings(ClientSettings settings, IConfigHandler specificSettings, Planet object) {

	}

	@Override
	public void updateCache(Planet object, PlanetImage image, ViewerInfo info, IStellarChecker checker) {
		appCoord.x = image.appCoord.x;
		appCoord.y = image.appCoord.y;
		
		double airmass = info.sky.calculateAirmass(this.appCoord);
		double appMag = (object.currentMag + airmass * OpticsHelper.ext_coeff_V);
		this.brightness = OpticsHelper.getBrightnessFromMagnitude(appMag);

		this.size = 0.6f;
		
		checker.startDescription();
		checker.brightness(brightness, brightness, brightness);
		checker.radius(this.size);
		this.shouldRender = checker.endCheckRendered();
		
		/*if(appCoord.y < 0.0 && info.hideObjectsUnderHorizon)
			this.shouldRender = false;*/
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ICelestialObjectRenderer getRenderer() {
		return PlanetRenderer.INSTANCE;
	}

}
