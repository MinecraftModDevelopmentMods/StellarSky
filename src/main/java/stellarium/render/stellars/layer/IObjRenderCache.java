package stellarium.render.stellars.layer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import stellarium.lib.render.IRenderModel;
import stellarium.stellars.layer.StellarObject;
import stellarium.stellars.render.ICelestialObjectRenderer;

public interface IObjRenderCache<Obj extends StellarObject> extends IRenderModel<ObjectSettings, ObjectUpdateInfo> {

	@SideOnly(Side.CLIENT)
	ICelestialObjectRenderer getRenderer();

}
