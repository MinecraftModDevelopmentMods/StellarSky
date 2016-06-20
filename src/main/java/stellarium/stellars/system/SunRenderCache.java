package stellarium.stellars.system;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Vector3;
import stellarium.client.ClientSettings;
import stellarium.render.stellars.access.IStellarChecker;
import stellarium.render.stellars.layer.IObjRenderCache;
import stellarium.stellars.render.ICelestialObjectRenderer;
import stellarium.util.math.Allocator;
import stellarium.view.ViewerInfo;

public class SunRenderCache implements IObjRenderCache<Sun, SunImage, SolarSystemClientSettings> {
	protected SpCoord appCoord = new SpCoord();
	protected float size;
	protected int latn, longn;
	
	protected SpCoord sunPos[][];
	protected Vector3 sunNormal[][];
	
	private Vector3 buf = new Vector3();

	@Override
	public void updateSettings(ClientSettings settings, SolarSystemClientSettings specificSettings, Sun object) {
		this.latn = specificSettings.imgFrac;
		this.longn = 2*specificSettings.imgFrac;
		
		this.sunPos = Allocator.createAndInitializeSp(longn, latn+1);
		this.sunNormal = Allocator.createAndInitialize(longn, latn+1);
	}

	@Override
	public void updateCache(Sun object, SunImage image, ViewerInfo info, IStellarChecker checker) {
		appCoord.x = image.appCoord.x;
		appCoord.y = image.appCoord.y;
		this.size = (float) (object.radius / object.earthPos.size());
		
		checker.startDescription();
		checker.brightness(1.0f, 1.0f, 1.0f);
		checker.pos(this.appCoord);
		checker.radius(this.size);
		checker.checkDominator();
		
		int latc, longc;
		for(longc=0; longc<longn; longc++){
			for(latc=0; latc<=latn; latc++){
				buf.set(object.posLocalSun((double)longc/(double)longn*360.0, (double)latc/(double)latn*180.0-90.0));
				
				sunNormal[longc][latc].set(buf);
				sunNormal[longc][latc].normalize();
				
				buf.add(object.earthPos);
				info.coordinate.getProjectionToGround().transform(buf);

				double size = buf.size();
				sunPos[longc][latc].setWithVec(buf);
				info.sky.applyAtmRefraction(sunPos[longc][latc]);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ICelestialObjectRenderer getRenderer() {
		return SunRenderer.INSTANCE;
	}



}
