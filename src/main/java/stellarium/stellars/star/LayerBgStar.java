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
import stellarium.stellars.layer.IStellarLayerType;
import stellarium.stellars.layer.StellarObjectContainer;
import stellarium.stellars.render.ICelestialLayerRenderer;

public abstract class LayerBgStar<ClientConfig extends IConfigHandler, CommonConfig extends INBTConfig> 
implements IStellarLayerType<BgStar, ClientConfig, CommonConfig> {
	
	public static int renderIndex = -1;
	public static int renderStarIndex = -1;
	
	@Override
	public void updateLayer(StellarObjectContainer<BgStar> container, double year) { }
	
	@Override
	public Comparator<ICelestialObject> getDistanceComparator(SpCoord pos) {
		return null;
	}

	@Override
	public Predicate<ICelestialObject> conditionInRange(SpCoord pos, double radius) {
		return null;
	}
	
	@Override
	public Collection<BgStar> getSuns(StellarObjectContainer<BgStar> container) {
		return null;
	}

	@Override
	public Collection<BgStar> getMoons(StellarObjectContainer<BgStar> container) {
		return null;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public ICelestialLayerRenderer getLayerRenderer() {
		return LayerStarRenderer.INSTANCE;
	}
}
