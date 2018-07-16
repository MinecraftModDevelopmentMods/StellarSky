package stellarium.stellars.star;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stellarapi.api.lib.config.IConfigHandler;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Vector3;
import stellarium.client.ClientSettings;
import stellarium.render.stellars.layer.IObjRenderCache;
import stellarium.render.stellars.layer.LayerRHelper;
import stellarium.stellars.OpticsHelper;
import stellarium.stellars.render.ICelestialObjectRenderer;
import stellarium.stellars.util.StarColor;
import stellarium.view.ViewerInfo;

public class StarRenderCache implements IObjRenderCache<BgStar, IConfigHandler> {
	protected SpCoord appPos = new SpCoord();
	protected Vector3 pos = new Vector3();
	protected float red, green, blue;
	protected Vector3 ref = new Vector3();

	@Override
	public void updateSettings(ClientSettings settings, IConfigHandler config, BgStar star) { }

	@Override
	public void updateCache(BgStar object, ViewerInfo info) {
		ref.set(object.pos);
		info.coordinate.getProjectionToGround().transform(this.ref);
		pos.set(this.ref);
		pos.scale(LayerRHelper.DEEP_DEPTH);

		StarColor starColor = StarColor.getColor(object.B_V);

		double alpha = OpticsHelper.getBrightnessFromMag(OpticsHelper.turbulance() + object.mag);
		this.red = (float) (alpha * starColor.r / 255.0);
		this.green = (float) (alpha * starColor.g / 255.0);
		this.blue = (float) (alpha * starColor.b / 255.0);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ICelestialObjectRenderer getRenderer() {
		return StarRenderer.INSTANCE;
	}

}
