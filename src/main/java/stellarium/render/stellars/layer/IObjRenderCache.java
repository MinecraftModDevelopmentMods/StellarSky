package stellarium.render.stellars.layer;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stellarapi.api.lib.config.IConfigHandler;
import stellarium.client.ClientSettings;
import stellarium.stellars.layer.StellarObject;
import stellarium.stellars.render.ICelestialObjectRenderer;
import stellarium.view.ViewerInfo;

public interface IObjRenderCache<S extends StellarObject, C extends IConfigHandler> {

	public void updateSettings(ClientSettings settings, C specificSettings, S object);

	/**
	 * Updates this render cache.
	 * @param object the object
	 * @param image the image for this object, can be null
	 * */
	public void updateCache(S object, ViewerInfo info);

	@SideOnly(Side.CLIENT)
	ICelestialObjectRenderer getRenderer();

}