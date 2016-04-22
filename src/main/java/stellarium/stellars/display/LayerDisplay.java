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
import stellarium.render.StellarRenderingRegistry;
import stellarium.stellars.layer.StellarObject;
import stellarium.stellars.layer.StellarObjectContainer;
import stellarium.stellars.layer.IPerWorldImage;
import stellarium.stellars.layer.IStellarLayerType;

public class LayerDisplay implements IStellarLayerType<DisplayElement, DisplaySettings, INBTConfig> {
		
	public List<DisplayElement> displayElements;

	@Override
	public void initializeClient(DisplaySettings config, StellarObjectContainer container) throws IOException {
		this.displayElements = config.getDisplayElements();
		
		for(DisplayElement element : this.displayElements)
		{
			container.loadObject("Display", element);
			container.addRenderCache(element, element.getCache());
		}
	}
	
	@Override
	public void initializeCommon(INBTConfig config, StellarObjectContainer container) throws IOException { }
	
	@Override
	public void updateLayer(StellarObjectContainer<DisplayElement, DisplaySettings> container, double year) { }

	@Override
	public int getLayerRendererIndex() {
		return -1;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerRenderers() {
		for(DisplayElement element : this.displayElements)
		{
			int id = StellarRenderingRegistry.getInstance().registerObjectRenderer(element.getRenderer());
			element.setRenderId(id);
		}
	}

	@Override
	public String getName() {
		return "Display";
	}

	@Override
	public int searchOrder() {
		// Will never search this element.
		return 100;
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
	public Map<DisplayElement, IPerWorldImage> temporalLoadImagesInRange(SpCoord pos, double radius) {
		return null;
	}

	@Override
	public Collection<DisplayElement> getSuns(StellarObjectContainer container) {
		return null;
	}

	@Override
	public Collection<DisplayElement> getMoons(StellarObjectContainer container) {
		return null;
	}
}
