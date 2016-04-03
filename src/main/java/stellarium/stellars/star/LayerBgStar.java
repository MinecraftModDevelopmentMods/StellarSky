package stellarium.stellars.star;

import java.io.IOException;
import java.util.List;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import stellarium.stellars.layer.CelestialObject;
import stellarium.stellars.layer.CelestialRenderingRegistry;
import stellarium.stellars.layer.ICelestialLayer;

public abstract class LayerBgStar implements ICelestialLayer {
	
	public static int renderIndex = -1;
		
	public abstract List<? extends CelestialObject> getObjectList();

	@Override
	public int getLayerRendererIndex() {
		return renderIndex;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerRenderers() {
		if(renderIndex == -1) {
			CelestialRenderingRegistry registry = CelestialRenderingRegistry.getInstance();
			renderIndex = registry.registerLayerRenderer(new LayerStarRenderer());
			BgStar.setRenderId(registry.registerObjectRenderer(new StarRenderer()));
		}
	}
	
	@Override
	public void updateLayer(double year) { }
}
