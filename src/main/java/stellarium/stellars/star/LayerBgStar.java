package stellarium.stellars.star;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import stellarapi.api.lib.config.IConfigHandler;
import stellarapi.api.lib.config.INBTConfig;
import stellarium.render.CelestialRenderingRegistry;
import stellarium.stellars.layer.StellarObject;
import stellarium.stellars.layer.StellarObjectContainer;
import stellarium.stellars.layer.IStellarLayerType;

public abstract class LayerBgStar<ClientConfig extends IConfigHandler, CommonConfig extends INBTConfig> 
implements IStellarLayerType<BgStar, ClientConfig, CommonConfig> {
	
	public static int renderIndex = -1;
	public static int renderStarIndex = -1;
	
	@Override
	public int getLayerRendererIndex() {
		return renderIndex;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerRenderers() {
		CelestialRenderingRegistry registry = CelestialRenderingRegistry.getInstance();
		renderIndex = registry.registerLayerRenderer(new LayerStarRenderer());
		renderStarIndex = registry.registerObjectRenderer(new StarRenderer());
	}
	
	@Override
	public void updateLayer(StellarObjectContainer<BgStar, ClientConfig> container, double year) { }
}
