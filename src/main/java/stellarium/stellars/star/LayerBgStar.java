package stellarium.stellars.star;

import java.util.Collection;
import java.util.Comparator;

import com.google.common.base.Predicate;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stellarapi.api.celestials.ICelestialObject;
import stellarapi.api.lib.config.IConfigHandler;
import stellarapi.api.lib.config.INBTConfig;
import stellarapi.api.lib.math.SpCoord;
import stellarium.render.StellarRenderingRegistry;
import stellarium.stellars.layer.IStellarLayerType;
import stellarium.stellars.layer.StellarObjectContainer;

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
		StellarRenderingRegistry registry = StellarRenderingRegistry.getInstance();
		renderIndex = registry.registerLayerRenderer(new LayerStarRenderer());
		renderStarIndex = registry.registerObjectRenderer(new StarRenderer());
	}
	
	@Override
	public void updateLayer(StellarObjectContainer<BgStar, ClientConfig> container, double year) { }
	
	@Override
	public Comparator<ICelestialObject> getDistanceComparator(SpCoord pos) {
		return null;
	}

	@Override
	public Predicate<ICelestialObject> conditionInRange(SpCoord pos, double radius) {
		return null;
	}
	
	@Override
	public Collection<BgStar> getSuns(StellarObjectContainer container) {
		return null;
	}

	@Override
	public Collection<BgStar> getMoons(StellarObjectContainer container) {
		return null;
	}
}
