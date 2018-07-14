package stellarium.stellars.star;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stellarapi.api.celestials.EnumCollectionType;
import stellarapi.api.lib.config.IConfigHandler;
import stellarapi.api.lib.config.INBTConfig;
import stellarium.stellars.layer.StellarLayer;
import stellarium.stellars.render.ICelestialLayerRenderer;

public abstract class LayerBgStar<ClientConfig extends IConfigHandler, CommonConfig extends INBTConfig> 
extends StellarLayer<BgStar, ClientConfig, CommonConfig> {
	
	public LayerBgStar(ResourceLocation nameIn, int order) {
		super(nameIn, EnumCollectionType.Stars, order);
	}

	public static int renderIndex = -1;
	public static int renderStarIndex = -1;

	@SideOnly(Side.CLIENT)
	@Override
	public ICelestialLayerRenderer getLayerRenderer() {
		return LayerStarRenderer.INSTANCE;
	}
}
