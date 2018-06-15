package stellarium.stellars.system;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Vector3;
import stellarapi.api.optics.Wavelength;
import stellarium.client.ClientSettings;
import stellarium.render.stellars.access.IStellarChecker;
import stellarium.render.stellars.layer.IObjRenderCache;
import stellarium.stellars.OpticsHelper;
import stellarium.stellars.render.ICelestialObjectRenderer;
import stellarium.util.math.Allocator;
import stellarium.view.ViewerInfo;

public class MoonRenderCache implements IObjRenderCache<Moon, MoonImage, SolarSystemClientSettings> {
	
	protected boolean shouldRenderDominate, shouldRender;
	
	protected SpCoord appCoord;
	protected Vector3 appPos;
	private SpCoord cache = new SpCoord();
	protected int latn, longn;

	protected Vector3 pos[][];
	protected Vector3 normal[][];
	protected float surfBr[][];

	protected float domination, brightness;

	protected Vector3 buf = new Vector3();
	protected float size;

	@Override
	public void updateSettings(ClientSettings settings, SolarSystemClientSettings specificSettings, Moon object) {
		this.appCoord = new SpCoord();
		this.appPos = new Vector3();

		this.latn = specificSettings.imgFrac;
		this.longn = 2*specificSettings.imgFrac;

		this.pos = Allocator.createAndInitialize(longn, latn+1);
		this.surfBr = new float[longn][latn+1];
		this.normal = Allocator.createAndInitialize(longn, latn+1);
	}

	@Override
	public void updateCache(Moon object, MoonImage image, ViewerInfo info, IStellarChecker checker) {
		SpCoord currentPos = image.getCurrentHorizontalPos();
		appCoord.x = currentPos.x;
		appCoord.y = currentPos.y;
		appPos.set(appCoord.getVec());

		double airmass = info.sky.calculateAirmass(this.appCoord);
		double appMag = object.currentMag + airmass * info.sky.getExtinctionRate(Wavelength.visible);
		this.domination = OpticsHelper.getDominationFromMag(appMag);

		this.size = (float) (object.radius / object.earthPos.size());
		checker.startDescription();
		checker.brightness(domination, domination, domination);
		this.shouldRenderDominate = checker.checkDominator();
		
		this.brightness = OpticsHelper.getBrightnessFromMag(appMag);
		
		checker.startDescription();
		checker.brightness(brightness, brightness, brightness);
		checker.pos(new SpCoord(0.0, 90.0));
		checker.radius(this.size);
		this.shouldRender = checker.checkRendered();

		if(!this.shouldRender)
			return;

		int latc, longc;
		for(longc=0; longc<longn; longc++){
			for(latc=0; latc<=latn; latc++){
				buf.set(object.posLocalM((double)longc/(double)longn*360.0, (double)latc/(double)latn*180.0-90.0));
				
				surfBr[longc][latc] = (float) Math.max(object.illumination(buf), 0.0);
				normal[longc][latc].set(buf);
				normal[longc][latc].normalize();
				
				buf.set(object.posLocalG(buf));
				info.coordinate.getProjectionToGround().transform(buf);

				cache.setWithVec(buf);
				info.sky.applyAtmRefraction(cache);
				pos[longc][latc].set(cache.getVec());
			}
		}
		
		this.brightness *= 1.0e-5f;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ICelestialObjectRenderer getRenderer() {
		return MoonRenderer.INSTANCE;
	}

}
