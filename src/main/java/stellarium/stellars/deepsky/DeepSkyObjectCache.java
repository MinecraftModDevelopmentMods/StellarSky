package stellarium.stellars.deepsky;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.ResourceLocation;
import stellarapi.api.lib.config.IConfigHandler;
import stellarapi.api.lib.math.Matrix3;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Spmath;
import stellarapi.api.lib.math.Vector3;
import stellarapi.api.optics.Wavelength;
import stellarium.client.ClientSettings;
import stellarium.render.stellars.access.IStellarChecker;
import stellarium.render.stellars.layer.IObjRenderCache;
import stellarium.stellars.OpticsHelper;
import stellarium.stellars.render.ICelestialObjectRenderer;
import stellarium.view.ViewerInfo;

public class DeepSkyObjectCache implements IObjRenderCache<DeepSkyObject, DeepSkyImage, IConfigHandler> {

	//Zero-time axial tilt
	public static final double e=0.4090926;
	public static final Matrix3 EqtoEc = new Matrix3();
	
	static {
		EqtoEc.setAsRotation(1.0, 0.0, 0.0, -e);
	}

	protected SpCoord[] coords = new SpCoord[4];
	protected ResourceLocation location;
	protected float alpha;
	protected boolean shouldRender;
	private Vector3[] quads = new Vector3[4];

	@Override
	public void updateSettings(ClientSettings settings, IConfigHandler specificSettings, DeepSkyObject object) {
		for(int i = 0; i < 4; i++) {
			this.coords[i] = new SpCoord();
			this.quads[i] = new Vector3();
		}

		this.location = object.getTexture().getTextureLocation();
	}

	@Override
	public void updateCache(DeepSkyObject object, DeepSkyImage image, ViewerInfo info, IStellarChecker checker) {		
		DeepSkyTexture texture = object.getTexture();
		
		Vector3 center = new Vector3(object.centerPos);
		Vector3 dirWidth = new Vector3().setCross(center, new Vector3(0.0, 0.0, 1.0)).normalize();
		Vector3 dirHeight = new Vector3().setCross(dirWidth, center).normalize();
		texture.fill(center, dirWidth, dirHeight, this.quads);

		for(int i = 0; i < 4; i++) {
			EqtoEc.transform(quads[i]);
			info.coordinate.getProjectionToGround().transform(quads[i]);
			coords[i].setWithVec(quads[i]);
		}
		
		double airmass = info.sky.calculateAirmass(image.getCurrentHorizontalPos());
		double magnitude = object.magnitude + airmass * info.sky.getExtinctionRate(Wavelength.visible);
		this.alpha = OpticsHelper.getBrightnessFromMagnitude(magnitude) * (float)(Spmath.sqr(0.015f) / object.getSurfaceSize());

		checker.startDescription();
		checker.pos(this.coords[0]);
		checker.brightness(this.alpha, this.alpha, this.alpha);
		this.shouldRender = checker.checkRendered();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ICelestialObjectRenderer getRenderer() {
		return DSObjectRenderer.INSTANCE;
	}


}
