package stellarium.display;

import com.google.common.collect.ImmutableList;

import stellarium.client.ClientSettings;
import stellarium.display.ecgrid.EcGridType;
import stellarium.display.eqgrid.EqGridType;
import stellarium.display.horgrid.HorGridType;

public class DisplayRegistry {
	
	private static final DisplayRegistry instance = new DisplayRegistry();
	
	public static DisplayRegistry getInstance() {
		return instance;
	}

	static {
		// MAYBE Interaction with existing objects
		instance.register(new HorGridType());
		instance.register(new EqGridType());
		instance.register(new EcGridType());
	}

	private ImmutableList.Builder<RegistryDelegate> builder = ImmutableList.builder();
	
	public <Cfg extends PerDisplaySettings, Cache extends IDisplayCache<Cfg>>
	void register(IDisplayElementType<Cfg, Cache> type) {
		builder.add(new RegistryDelegate<Cfg, Cache>(type));
	}
	
	public void composeSettings(ClientSettings settings) {
		DisplayOverallSettings displaySettings = new DisplayOverallSettings();
		settings.putSubConfig("Display", displaySettings);
		for(RegistryDelegate delegate : builder.build())
			displaySettings.putSubConfig(delegate.type.getName(), delegate.perDisplay);
	}
	
	public void setupDisplay(IDisplayInjectable injectable) {
		for(RegistryDelegate delegate : builder.build())
			injectable.injectDisplay(delegate.type, delegate.perDisplay);
	}
	
	private static class RegistryDelegate<Cfg extends PerDisplaySettings, Cache extends IDisplayCache<Cfg>> {
		private IDisplayElementType<Cfg, Cache> type;
		private Cfg perDisplay;
		
		private RegistryDelegate(IDisplayElementType<Cfg, Cache> type) {
			this.type = type;
			this.perDisplay = type.generateSettings();
		}
	}
}
