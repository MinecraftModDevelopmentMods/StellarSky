package stellarium.client.gui.content;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;

public interface IRenderModel {

	/**
	 * Do not call GL11 matrix transformation/color call here,
	 * except for scale/rotation.
	 * */
	void renderModel(String info, IRectangleBound totalBound, IRectangleBound clipBound,
			Tessellator tessellator, TextureManager textureManager);

}
