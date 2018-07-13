package stellarium.stellars.deepsky;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stellarapi.api.lib.config.IConfigHandler;
import stellarapi.api.lib.math.Matrix3;
import stellarapi.api.lib.math.Vector3;
import stellarium.client.ClientSettings;
import stellarium.render.stellars.layer.IObjRenderCache;
import stellarium.render.stellars.layer.LayerRHelper;
import stellarium.stellars.OpticsHelper;
import stellarium.stellars.render.ICelestialObjectRenderer;
import stellarium.view.ViewerInfo;

public class DeepSkyObjectCache implements IObjRenderCache<DeepSkyObject, IConfigHandler> {

	//Zero-time axial tilt
	public static final double e=0.4090926;
	public static final Matrix3 EqtoEc = new Matrix3();
	
	static {
		EqtoEc.setAsRotation(1.0, 0.0, 0.0, -e);
	}

	protected Vector3[] coords = new Vector3[4];
	protected ResourceLocation location;
	protected float surfBr;
	protected boolean shouldRender;
	private Vector3[] quads = new Vector3[4];

	@Override
	public void updateSettings(ClientSettings settings, IConfigHandler specificSettings, DeepSkyObject object) {
		for(int i = 0; i < 4; i++) {
			this.coords[i] = new Vector3();
			this.quads[i] = new Vector3();
		}

		if(object.getTexture().isPresent())
			this.location = object.getTexture().get().getTextureLocation();
	}

	@Override
	public void updateCache(DeepSkyObject object, ViewerInfo info) {		
		if(!object.getTexture().isPresent()) {
			this.shouldRender = false;
			return;
		}

		DeepSkyTexture texture = object.getTexture().get();

		Vector3 center = new Vector3(object.centerPos);
		Vector3 dirWidth = new Vector3().setCross(center, new Vector3(0.0, 0.0, 1.0)).normalize();
		Vector3 dirHeight = new Vector3().setCross(dirWidth, center).normalize();
		texture.fill(center, dirWidth, dirHeight, this.quads);

		for(int i = 0; i < 4; i++) {
			EqtoEc.transform(quads[i]);
			info.coordinate.getProjectionToGround().transform(quads[i]);
			coords[i].set(quads[i]);
			coords[i].scale(LayerRHelper.DEEP_DEPTH);
		}

		double magnitude = object.magnitude;
		this.surfBr = OpticsHelper.getBrightnessFromMag(magnitude)
				* OpticsHelper.getMultFromArea(object.getTexture().get().equivalentSize());
		this.shouldRender = true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ICelestialObjectRenderer<DeepSkyObjectCache> getRenderer() {
		return DSObjectRenderer.INSTANCE;
	}


}
