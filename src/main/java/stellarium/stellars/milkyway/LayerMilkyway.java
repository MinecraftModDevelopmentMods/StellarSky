package stellarium.stellars.milkyway;

import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import stellarapi.api.celestials.EnumCelestialCollectionType;
import stellarapi.api.celestials.ICelestialObject;
import stellarapi.api.lib.config.IConfigHandler;
import stellarapi.api.lib.config.INBTConfig;
import stellarapi.api.lib.math.SpCoord;
import stellarium.render.StellarRenderingRegistry;
import stellarium.stellars.layer.StellarObject;
import stellarium.stellars.layer.StellarObjectContainer;
import stellarium.stellars.layer.IPerWorldImage;
import stellarium.stellars.layer.IStellarLayerType;

public class LayerMilkyway implements IStellarLayerType<Milkyway, IConfigHandler, INBTConfig> {
	
	public static int milkywayRenderId;

	@Override
	public void initializeClient(IConfigHandler config, StellarObjectContainer container) throws IOException { }
	
	@Override
	public void initializeCommon(INBTConfig config, StellarObjectContainer container) throws IOException {
		Milkyway milkyway = new Milkyway();
		container.loadObject("Milkyway", milkyway);
		container.addRenderCache(milkyway, new MilkywayRenderCache());
		container.addImageType(milkyway, MilkywayImage.class);
	}
	
	@Override
	public void updateLayer(StellarObjectContainer<Milkyway, IConfigHandler> container, double year) { }

	@Override
	public int getLayerRendererIndex() {
		return -1;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerRenderers() {
		milkywayRenderId = StellarRenderingRegistry.getInstance().registerObjectRenderer(new MilkywayRenderer());
	}

	@Override
	public String getName() {
		return "Milkyway";
	}

	@Override
	public int searchOrder() {
		return 2;
	}

	@Override
	public boolean isBackground() {
		return true;
	}

	@Override
	public EnumCelestialCollectionType getCollectionType() {
		return EnumCelestialCollectionType.DeepSkyObjects;
	}

	@Override
	public Comparator<ICelestialObject> getDistanceComparator(SpCoord pos) {
		return Ordering.allEqual().reverse();
	}

	@Override
	public Predicate<ICelestialObject> conditionInRange(SpCoord pos, double radius) {
		return Predicates.alwaysTrue();
	}

	@Override
	public Map<Milkyway, IPerWorldImage> temporalLoadImagesInRange(SpCoord pos, double radius) {
		return null;
	}

	@Override
	public Collection<Milkyway> getSuns(StellarObjectContainer container) {
		return null;
	}

	@Override
	public Collection<Milkyway> getMoons(StellarObjectContainer container) {
		return null;
	}
}
