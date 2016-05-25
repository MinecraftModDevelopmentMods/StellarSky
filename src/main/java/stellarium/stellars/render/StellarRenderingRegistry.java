package stellarium.stellars.render;

import java.util.List;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class StellarRenderingRegistry {
	
	private static StellarRenderingRegistry INSTANCE;
	
	public static StellarRenderingRegistry getInstance() {
		if(INSTANCE == null)
			INSTANCE = new StellarRenderingRegistry();
		return INSTANCE;
	}
	
	private List<ICelestialLayerRenderer> layerRendererList = Lists.newArrayList();
	private List<ICelestialObjectRenderer> objectRendererList = Lists.newArrayList();
	
	protected void refresh() {
		layerRendererList.clear();
		objectRendererList.clear();
	}
	
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
