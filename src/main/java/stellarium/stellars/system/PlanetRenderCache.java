package stellarium.stellars.system;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stellarapi.api.lib.config.IConfigHandler;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Vector3;
import stellarapi.api.optics.Wavelength;
import stellarium.client.ClientSettings;
import stellarium.render.stellars.access.IStellarChecker;
import stellarium.render.stellars.layer.IObjRenderCache;
import stellarium.stellars.OpticsHelper;
import stellarium.stellars.render.ICelestialObjectRenderer;
import stellarium.view.ViewerInfo;

public class PlanetRenderCache implements IObjRenderCache<Planet, PlanetImage, IConfigHandler> {
	
	protected boolean shouldRender, shouldRenderSurface;
	protected SpCoord appCoord = new SpCoord();
	protected Vector3 pos = new Vector3();
	protected float brightness;
	protected float size;

	@Override
	public void updateSettings(ClientSettings settings, IConfigHandler specificSettings, Planet object) {

	}

	@Override
	public void updateCache(Planet object, PlanetImage image, ViewerInfo info, IStellarChecker checker) {
		appCoord.x = image.appCoord.x;
		appCoord.y = image.appCoord.y;

		pos.set(appCoord.getVec());

		double airmass = info.sky.calculateAirmass(this.appCoord);
		double appMag = (object.currentMag + airmass * info.sky.getExtinctionRate(Wavelength.visible));
		this.brightness = OpticsHelper.getBrightnessFromMag(appMag);

		this.size = (float) (object.radius / object.earthPos.size());

		checker.startDescription();
		checker.brightness(brightness, brightness, brightness);
		checker.pos(this.appCoord);
		checker.radius(this.size);
		this.shouldRender = checker.checkRendered();
		this.shouldRenderSurface = this.shouldRender && checker.checkEnoughRadius();
		// MAYBE planet rendering, which needs over 100x multiplier
		this.brightness *= 0.5f;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ICelestialObjectRenderer getRenderer() {
		return PlanetRenderer.INSTANCE;
	}

}
