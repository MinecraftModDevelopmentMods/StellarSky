package stellarium.stellars.system;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import stellarapi.api.lib.config.IConfigHandler;
import stellarapi.api.lib.math.SpCoord;
import stellarium.client.ClientSettings;
import stellarium.render.stellars.access.IStellarChecker;
import stellarium.render.stellars.layer.IObjRenderCache;
import stellarium.stellars.render.ICelestialObjectRenderer;
import stellarium.view.ViewerInfo;

public class SunRenderCache implements IObjRenderCache<Sun, SunImage, IConfigHandler> {
	protected SpCoord appCoord = new SpCoord();
	protected double size;

	@Override
	public void updateSettings(ClientSettings settings, IConfigHandler specificSettings, Sun object) {
	}

	@Override
	public void updateCache(Sun object, SunImage image, ViewerInfo info, IStellarChecker checker) {
		appCoord.x = image.appCoord.x;
		appCoord.y = image.appCoord.y;
		this.size = object.radius / object.earthPos.size()*99.0*20;
		
		/*checker.startDescription();
		checker.brightness(1.0f, 1.0f, 1.0f);
		checker.endCheckDominator();*/
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ICelestialObjectRenderer getRenderer() {
		return SunRenderer.INSTANCE;
	}



}
