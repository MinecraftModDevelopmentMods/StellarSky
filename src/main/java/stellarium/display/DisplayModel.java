package stellarium.display;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.world.World;
import stellarapi.api.lib.config.SimpleHierarchicalConfig;
import stellarium.client.ClientSettings;
import stellarium.lib.hierarchy.Hierarchy;
import stellarium.stellars.StellarManager;
import stellarium.view.ViewerInfo;
import stellarium.world.StellarScene;

@Hierarchy
public class DisplayModel implements IDisplayInjectable {

	List<Delegate> displayList = Lists.newArrayList();

	@Override
	public <Cfg extends PerDisplaySettings, Cache extends IDisplayCache<Cfg>> void injectDisplay(
			IDisplayElementType<Cfg, Cache> type, Cfg settings) {
		displayList.add(new Delegate(type, settings));
	}

	@Override
	public SimpleHierarchicalConfig getSubSettings(ClientSettings settings) {
		DisplayOverallSettings displaySettings = new DisplayOverallSettings();
		settings.putSubConfig("Display", displaySettings);
		return displaySettings;
	}

	public void initializeSettings(ClientSettings settings) {
		DisplayRegistry.getInstance().setupDisplay(settings, this);
	}

	public void updateSettings(ClientSettings settings) {
		for(Delegate delegate : this.displayList)
			delegate.cache.initialize(settings, delegate.settings);
	}

	public void stellarLoad(StellarManager manager) { }

	public void dimensionLoad(StellarScene dimManager) { }

	public void onTick(World world, ViewerInfo update) {
		DisplayCacheInfo info = new DisplayCacheInfo(update.coordinate, update.sky);
		for(Delegate delegate : this.displayList)
			delegate.cache.updateCache(info);
	}

	static class Delegate<Cfg extends PerDisplaySettings, Cache extends IDisplayCache<Cfg>> {
		public Delegate(IDisplayElementType<Cfg, Cache> type, Cfg settings) {
			this.type = type;
			this.settings = settings;
			this.cache = type.generateCache();
			this.renderer = type.getRenderer();
		}

		private IDisplayElementType<Cfg, Cache> type;
		Cache cache;
		private Cfg settings;
		IDisplayRenderer<Cache> renderer;
	}
}