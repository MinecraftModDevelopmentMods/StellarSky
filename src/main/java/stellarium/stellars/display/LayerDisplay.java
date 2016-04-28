package stellarium.stellars.display;

import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
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
import stellarium.stellars.display.eccoord.DisplayEcCoordType;
import stellarium.stellars.display.eqcoord.DisplayEqCoordType;
import stellarium.stellars.display.horcoord.DisplayHorCoordType;
import stellarium.stellars.layer.IPerWorldImage;
import stellarium.stellars.layer.IStellarLayerType;
import stellarium.stellars.layer.StellarObjectContainer;

public class LayerDisplay implements IStellarLayerType<DisplayElement, DisplaySettings, INBTConfig>, Callable<IConfigHandler> {
	
	private int renderId;
	private Ordering<IDisplayElementType> displayOrdering;
	ImmutableList<DisplayDelegate> displayElementDelegates;
	
	private static List<IDisplayElementType> listDisplayType = Lists.newArrayList(
			new DisplayHorCoordType(),
			new DisplayEqCoordType(),
			new DisplayEcCoordType());
	
	public LayerDisplay() {
		this.displayOrdering = Ordering.explicit(listDisplayType).reverse();
		ImmutableList.Builder<DisplayDelegate> builder = ImmutableList.builder();
		for(IDisplayElementType type : listDisplayType)
			builder.add(new DisplayDelegate(type));
		this.displayElementDelegates = builder.build();
	}

	@Override
	public void initializeClient(DisplaySettings config, StellarObjectContainer container) throws IOException {		
		for(DisplayDelegate delegate : this.displayElementDelegates)
		{
			DisplayElement element = new DisplayElement(delegate);
			container.loadObject("Display", element);
			container.addRenderCache(element, new WrappedDisplayRenderCache(delegate));
		}
	}
	
	@Override
	public void initializeCommon(INBTConfig config, StellarObjectContainer container) throws IOException { }
	
	@Override
	public void updateLayer(StellarObjectContainer<DisplayElement, DisplaySettings> container, double year) { }

	@Override
	public int getLayerRendererIndex() {
		return this.renderId;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerRenderers() {
		this.renderId = StellarRenderingRegistry.getInstance().registerLayerRenderer(new LayerDisplayRenderer());
		for(DisplayDelegate delegate : this.displayElementDelegates)
		{
			int id = StellarRenderingRegistry.getInstance().registerObjectRenderer(
					new WrappedDisplayRenderer(delegate));
			delegate.renderId = id;
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

	@Override
	public Ordering<DisplayElement> getOrdering() {
		return this.displayOrdering.onResultOf(new Function<DisplayElement, IDisplayElementType>() {
					@Override
					public IDisplayElementType apply(DisplayElement input) {
						return input.getType();
					}
				});
	}
	

	@Override
	public IConfigHandler call() throws Exception {
		return new DisplaySettings(this);
	}
	
	static class DisplayDelegate {
		private DisplayDelegate(IDisplayElementType input) {
			this.type = input;
		}
		
		int renderId = -1;
		IDisplayElementType type;
	}
}