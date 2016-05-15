package stellarium.stellars.star;

import stellarapi.api.lib.config.IConfigHandler;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Vector3;
import stellarapi.api.optics.EnumRGBA;
import stellarapi.api.optics.EyeDetector;
import stellarium.client.ClientSettings;
import stellarium.render.EnumRenderPass;
import stellarium.stellars.Optics;
import stellarium.stellars.layer.IRenderCache;
import stellarium.stellars.layer.StellarCacheInfo;
import stellarium.stellars.util.StarColor;
import stellarium.util.math.StellarMath;

public class StarRenderCache implements IRenderCache<BgStar, IConfigHandler> {
	
	protected boolean shouldRender;
	protected SpCoord appCoord = new SpCoord();
	protected Vector3 pos = new Vector3(), dif = new Vector3(), dif2 = new Vector3();
	protected float appMag;
	protected double[] color = new double[4];
	
	@Override
	public void initialize(ClientSettings settings, IConfigHandler config, BgStar star) { }

	@Override
	public void updateCache(ClientSettings settings, IConfigHandler config, BgStar object, StellarCacheInfo info) {
		Vector3 ref = new Vector3(object.pos);
		info.projectionToGround.transform(ref);
		appCoord.setWithVec(ref);
		double airmass = info.calculateAirmass(this.appCoord);
		info.applyAtmRefraction(this.appCoord);
		
		float size = (float) (info.getResolution(EnumRGBA.Alpha) / 0.3);
		float multiplier = (float)(info.lgp / (size * size * info.mp * info.mp));

		this.appMag = (float) (object.mag + airmass * info.getExtinctionRate(EnumRGBA.Alpha)
			+ StellarMath.LumToMagWithoutSize(multiplier));

		this.shouldRender = true;
		if(this.appMag > settings.mag_Limit)
		{
			this.shouldRender = false;
			return;
		}
		
		if(appCoord.y < 0.0 && info.hideObjectsUnderHorizon) {
			this.shouldRender = false;
			return;
		}
		
		StarColor starColor = StarColor.getColor(object.B_V);
		size *= 0.5f * EnumRenderPass.getDeepDepth() / 100.0;
		
		double alpha = Optics.getAlphaFromMagnitude(object.mag + airmass * info.getExtinctionRate(EnumRGBA.Alpha), 0.0f) * 0.0005;
		
		System.arraycopy(EyeDetector.getInstance().process(multiplier, info.filter,
				new double[] {
						starColor.r / 255.0 * StellarMath.MagToLumWithoutSize(airmass * info.getExtinctionRate(EnumRGBA.Red)),
						starColor.g / 255.0 * StellarMath.MagToLumWithoutSize(airmass * info.getExtinctionRate(EnumRGBA.Green)),
						starColor.b / 255.0 * StellarMath.MagToLumWithoutSize(airmass * info.getExtinctionRate(EnumRGBA.Blue)),
						alpha}
			), 0, this.color, 0, color.length);
		this.color[3] /= (alpha * multiplier);
		
		pos.set(appCoord.getVec());
		appCoord.y += 90.0;
		dif2.set(appCoord.getVec());
		appCoord.x += 90.0;
		appCoord.y = 0.0;
		dif.set(appCoord.getVec());
		
		pos.scale(EnumRenderPass.getDeepDepth());
		dif.scale(size);
		dif2.scale(-size);
	}

	@Override
	public int getRenderId() {
		return LayerBgStar.renderStarIndex;
	}

}
