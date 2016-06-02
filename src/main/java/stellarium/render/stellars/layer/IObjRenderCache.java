package stellarium.render.stellars.layer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import stellarapi.api.lib.config.IConfigHandler;
import stellarium.client.ClientSettings;
import stellarium.render.stellars.access.IStellarChecker;
import stellarium.stellars.layer.IPerWorldImage;
import stellarium.stellars.layer.StellarObject;
import stellarium.stellars.render.ICelestialObjectRenderer;
import stellarium.view.ViewerInfo;

public interface IObjRenderCache<Obj extends StellarObject, Image extends IPerWorldImage, Config extends IConfigHandler> {

	public void updateSettings(ClientSettings settings, Config specificSettings, Obj object);

	public void updateCache(Obj object, Image perWorldImage, ViewerInfo info, IStellarChecker checker);

	@SideOnly(Side.CLIENT)
	ICelestialObjectRenderer getRenderer();

}