package stellarium.stellars.display;

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
import stellarium.render.CelestialRenderingRegistry;
import stellarium.stellars.layer.StellarObject;
import stellarium.stellars.layer.StellarObjectContainer;
import stellarium.stellars.layer.IPerWorldImage;
import stellarium.stellars.layer.IStellarLayerType;

public class LayerDisplay implements IStellarLayerType<Display, IConfigHandler, INBTConfig> {
	
	public static int displayRenderId;

	@Override
	public void initializeClient(IConfigHandler config, StellarObjectContainer container) throws IOException { }
	
	@Override
	public void initializeCommon(INBTConfig config, StellarObjectContainer container) throws IOException {
		Display display = new Display();
		container.loadObject("Display", display);
		container.addRenderCache(display, new DisplayRenderCache());
	}
	
	@Override
	public void updateLayer(StellarObjectContainer<Display, IConfigHandler> container, double year) { }

	@Override
	public int getLayerRendererIndex() {
		return -1;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerRenderers() {
		displayRenderId = CelestialRenderingRegistry.getInstance().registerObjectRenderer(new DisplayRenderer());
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
	public Map<Display, IPerWorldImage> temporalLoadImagesInRange(SpCoord pos, double radius) {
		return null;
	}

	@Override
	public Collection<Display> getSuns(StellarObjectContainer container) {
		return null;
	}

	@Override
	public Collection<Display> getMoons(StellarObjectContainer container) {
		return null;
	}
}
