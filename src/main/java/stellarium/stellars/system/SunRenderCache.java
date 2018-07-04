package stellarium.stellars.system;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Vector3;
import stellarium.client.ClientSettings;
import stellarium.render.stellars.layer.IObjRenderCache;
import stellarium.stellars.render.ICelestialObjectRenderer;
import stellarium.util.math.Allocator;
import stellarium.view.ViewerInfo;

public class SunRenderCache implements IObjRenderCache<Sun, SunImage, SolarSystemClientSettings> {
	protected SpCoord appCoord = new SpCoord();
	protected Vector3 appPos = new Vector3();
	protected float size;
	protected int latn, longn;

	protected SpCoord cache = new SpCoord();
	protected Vector3 sunPos[][];
	protected Vector3 sunNormal[][];
	
	private Vector3 buf = new Vector3();

	@Override
	public void updateSettings(ClientSettings settings, SolarSystemClientSettings specificSettings, Sun object) {
		this.latn = specificSettings.imgFrac;
		this.longn = 2*specificSettings.imgFrac;
		
		this.sunPos = Allocator.createAndInitialize(longn, latn+1);
		this.sunNormal = Allocator.createAndInitialize(longn, latn+1);
	}

	@Override
	public void updateCache(Sun object, SunImage image, ViewerInfo info) {
		//appCoord.x = image.appCoord.x;
		//appCoord.y = image.appCoord.y;
		//appPos.set(appCoord.getVec());
		appPos.set(object.earthPos);
		info.coordinate.getProjectionToGround().transform(this.appPos);
		appPos.normalize();

		this.size = (float) (object.radius / object.earthPos.size());
		
		int latc, longc;
		for(longc=0; longc<longn; longc++){
			for(latc=0; latc<=latn; latc++){
				buf.set(object.posLocalSun((double)longc/(double)longn*360.0, (double)latc/(double)latn*180.0-90.0));
				
				sunNormal[longc][latc].set(buf);
				sunNormal[longc][latc].normalize();
				
				buf.add(object.earthPos);
				info.coordinate.getProjectionToGround().transform(buf);

				//cache.setWithVec(buf);
				//info.sky.applyAtmRefraction(cache);
				buf.normalize();
				sunPos[longc][latc].set(buf);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ICelestialObjectRenderer getRenderer() {
		return SunRenderer.INSTANCE;
	}



}
