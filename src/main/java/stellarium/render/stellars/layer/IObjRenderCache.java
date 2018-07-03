package stellarium.render.stellars.layer;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stellarapi.api.lib.config.IConfigHandler;
import stellarium.client.ClientSettings;
import stellarium.stellars.layer.IPerWorldImage;
import stellarium.stellars.layer.StellarObject;
import stellarium.stellars.render.ICelestialObjectRenderer;
import stellarium.view.ViewerInfo;

public interface IObjRenderCache<Obj extends StellarObject, Image extends IPerWorldImage<Obj>, Config extends IConfigHandler> {

	public void updateSettings(ClientSettings settings, Config specificSettings, Obj object);

	/**
	 * Updates this render cache.
	 * @param object the object
	 * @param image the image for this object, can be null
	 * */
	public void updateCache(Obj object, Image image, ViewerInfo info);

	@SideOnly(Side.CLIENT)
	ICelestialObjectRenderer getRenderer();

}