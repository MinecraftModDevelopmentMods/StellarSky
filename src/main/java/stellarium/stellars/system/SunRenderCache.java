package stellarium.stellars.system;

import stellarapi.api.lib.config.IConfigHandler;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Vector3;
import stellarium.client.ClientSettings;
import stellarium.render.EnumRenderPass;
import stellarium.stellars.layer.IRenderCache;
import stellarium.stellars.layer.StellarCacheInfo;

public class SunRenderCache implements IRenderCache<Sun, IConfigHandler> {
	protected SpCoord appCoord = new SpCoord();
	protected Vector3 pos = new Vector3(), dif = new Vector3(), dif2 = new Vector3();

	@Override
	public void initialize(ClientSettings settings, IConfigHandler config, Sun sun) { }

	@Override
	public void updateCache(ClientSettings settings, IConfigHandler config, Sun object, StellarCacheInfo info) {
		Vector3 ref = new Vector3(object.earthPos);
		info.projectionToGround.transform(ref);
		appCoord.setWithVec(ref);
		info.applyAtmRefraction(this.appCoord);
		
		double size = object.radius / object.earthPos.size()*99.0*20;
		
		pos.set(appCoord.getVec());
		dif.set(new SpCoord(appCoord.x+90, 0.0).getVec());
		dif2.set(new SpCoord(appCoord.x, appCoord.y+90).getVec());

		pos.scale(EnumRenderPass.DEFAULT_OPAQUE_DEPTH);
		dif.scale(size);
		dif2.scale(-size);
	}

	@Override
	public int getRenderId() {
		return LayerSolarSystem.sunRenderId;
	}

}
