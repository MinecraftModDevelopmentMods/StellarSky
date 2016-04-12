package stellarium.stellars.star;

import java.util.List;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stellarium.render.CelestialRenderingRegistry;
import stellarium.stellars.layer.CelestialObject;
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
		CelestialRenderingRegistry registry = CelestialRenderingRegistry.getInstance();
		renderIndex = registry.registerLayerRenderer(new LayerStarRenderer());
		BgStar.setRenderId(registry.registerObjectRenderer(new StarRenderer()));
	}
	
	@Override
	public void updateLayer(double year) { }
}
