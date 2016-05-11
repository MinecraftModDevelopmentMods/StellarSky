package stellarium.client.overlay.clientcfg.model;

import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import stellarapi.lib.gui.IRectangleBound;
import stellarapi.lib.gui.IRenderModel;

public class ModelMain implements IRenderModel {

	public static final String separator = "\n";
	
	private Map<String, IRenderModel> subModels = Maps.newHashMap();
		
	public void addSubModel(String name, IRenderModel model) {
		subModels.put(name, model);
	}

	@Override
	public void renderModel(String info, IRectangleBound totalBound, IRectangleBound clipBound, Tessellator tessellator,
			TextureManager textureManager, float[] colors) {
		int index = info.lastIndexOf(separator);
		if(index == -1) {
			if(subModels.containsKey(info))
				subModels.get(info).renderModel("", totalBound, clipBound, tessellator, textureManager, colors);
		} else if(subModels.containsKey(info.substring(0, index)))
			subModels.get(info.substring(0, index)).renderModel(
					info.substring(index+1), totalBound, clipBound, tessellator, textureManager, colors);
	}

}
