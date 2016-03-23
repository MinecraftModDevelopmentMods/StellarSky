package stellarium.stellars.sketch;

import java.util.List;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CelestialRenderingRegistry {
	
	private static CelestialRenderingRegistry INSTANCE;
	
	public static CelestialRenderingRegistry getInstance() {
		if(INSTANCE == null)
			INSTANCE = new CelestialRenderingRegistry();
		return INSTANCE;
	}
	
	private List<ICelestialLayerRenderer> layerRendererList = Lists.newArrayList();
	private List<ICelestialObjectRenderer> objectRendererList = Lists.newArrayList();
	
	public int registerLayerRenderer(ICelestialLayerRenderer renderer) {
		int index = layerRendererList.size();
		layerRendererList.add(renderer);
		return index;
	}
	
	public int registerObjectRenderer(ICelestialObjectRenderer renderer) {
		int index = objectRendererList.size();
		objectRendererList.add(renderer);
		return index;
	}
	
	public ICelestialLayerRenderer getLayerRenderer(int index) {
		return layerRendererList.get(index);
	}
	
	public ICelestialObjectRenderer getObjectRenderer(int index) {
		return objectRendererList.get(index);
	}

}
