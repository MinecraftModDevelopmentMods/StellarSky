package stellarium.stellars.system;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Vector3;
import stellarapi.api.optics.EnumRGBA;
import stellarapi.api.optics.EyeDetector;
import stellarapi.api.optics.Wavelength;
import stellarium.client.ClientSettings;
import stellarium.render.stellars.access.IStellarChecker;
import stellarium.render.stellars.layer.IObjRenderCache;
import stellarium.stellars.OpticsHelper;
import stellarium.stellars.layer.StellarCacheInfo;
import stellarium.stellars.render.ICelestialObjectRenderer;
import stellarium.util.math.Allocator;
import stellarium.view.ViewerInfo;

public class MoonRenderCache implements IObjRenderCache<Moon, MoonImage, SolarSystemClientSettings> {
	
	protected boolean shouldRenderDominate, shouldRender;
	
	protected SpCoord appCoord;
	private SpCoord cache;
	protected int latn, longn;

	protected SpCoord moonPos[][];
	protected Vector3 moonnormal[][];
	protected float moonilum[][];
	
	protected float domination, brightness;

	protected Vector3 buf = new Vector3();
	protected float size;

	@Override
	public void updateSettings(ClientSettings settings, SolarSystemClientSettings specificSettings, Moon object) {
		this.appCoord = new SpCoord();
		
		this.latn = specificSettings.imgFrac;
		this.longn = 2*specificSettings.imgFrac;
		
		this.moonPos = Allocator.createAndInitializeSp(longn, latn+1);
		this.moonilum = new float[longn][latn+1];
		this.moonnormal = Allocator.createAndInitialize(longn, latn+1);
		
		this.cache = new SpCoord();
	}

	@Override
	public void updateCache(Moon object, MoonImage image, ViewerInfo info, IStellarChecker checker) {
		SpCoord currentPos = image.getCurrentHorizontalPos();
		appCoord.x = currentPos.x;
		appCoord.y = currentPos.y;

		double airmass = info.sky.calculateAirmass(this.appCoord);
		double appMag = object.currentMag + airmass * info.sky.getExtinctionRate(Wavelength.visible);
		this.domination = OpticsHelper.getDominationFromMagnitude(appMag);

		this.size = (float) (object.radius / object.earthPos.size());
		checker.startDescription();
		checker.brightness(domination, domination, domination);
		this.shouldRenderDominate = checker.endCheckDominator();
		
		this.brightness = OpticsHelper.getBrightnessFromMagnitude(appMag);
		
		checker.startDescription();
		checker.brightness(brightness, brightness, brightness);
		checker.radius(this.size);
		this.shouldRender = checker.endCheckRendered();

		if(!this.shouldRender)
			return;

		int latc, longc;
		for(longc=0; longc<longn; longc++){
			for(latc=0; latc<=latn; latc++){
				buf.set(object.posLocalM((double)longc/(double)longn*360.0, (double)latc/(double)latn*180.0-90.0));
				
				moonilum[longc][latc] = (float) Math.max(object.illumination(buf), 0.0);
				moonnormal[longc][latc].set(buf);
				moonnormal[longc][latc].normalize();
				
				buf.set(object.posLocalG(buf));
				info.coordinate.getProjectionToGround().transform(buf);

				double size = buf.size();
				moonPos[longc][latc].setWithVec(buf);
				info.sky.applyAtmRefraction(moonPos[longc][latc]);
			}
		}
		
		this.brightness *= 0.00001f;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ICelestialObjectRenderer getRenderer() {
		return MoonRenderer.INSTANCE;
	}

}
