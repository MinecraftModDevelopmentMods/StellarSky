package stellarium.stellars.layer;

import java.util.List;
import java.util.concurrent.Callable;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

import stellarapi.api.lib.config.IConfigHandler;
import stellarapi.api.lib.config.INBTConfig;
import stellarium.client.ClientSettings;
import stellarium.common.ServerSettings;
import stellarium.stellars.deepsky.LayerDeepSky;
import stellarium.stellars.milkyway.LayerMilkyway;
import stellarium.stellars.milkyway.MilkywaySettings;
import stellarium.stellars.star.brstar.LayerBrStar;
import stellarium.stellars.system.LayerSolarSystem;
import stellarium.stellars.system.SolarSystemClientSettings;
import stellarium.stellars.system.SolarSystemSettings;

public class StellarLayerRegistry {
	
	private static StellarLayerRegistry INSTANCE;
	
	public static StellarLayerRegistry getInstance() {
		if(INSTANCE == null)
			INSTANCE = new StellarLayerRegistry();
		return INSTANCE;
	}
	
	private List<RegistryDelegate> registeredLayers = Lists.newArrayList();

	public StellarLayerRegistry() {
		this.registerLayer(new LayerBrStar(), null);
		this.registerLayer(new LayerMilkyway(), "MilkyWay").clientConfig(MilkywaySettings::new);
		this.registerLayer(new LayerSolarSystem(), "SolarSystem")
		.commonConfig(SolarSystemSettings::new).clientConfig(SolarSystemClientSettings::new);
		this.registerLayer(new LayerDeepSky(), "DeepSky");
	}

	public RegistryDelegate registerLayer(StellarLayer layer, String configName)
	{
		RegistryDelegate delegate = new RegistryDelegate();
		delegate.layer = layer;
		delegate.configName = configName;

		registeredLayers.add(delegate);
		return delegate;
	}

	public void composeLayer(boolean isRemote, List<StellarCollection> list) {
		for(RegistryDelegate delegate : this.registeredLayers)
			try {
				list.add(new StellarCollection(delegate.layer, delegate.configName));
			} catch (Exception e) {
				Throwables.propagateIfPossible(e);
			}
	}
	
	public void composeSettings(ServerSettings settings) {
		for(RegistryDelegate delegate : this.registeredLayers)
			if(delegate.commonConfigCallable != null) {
				try {
					settings.putSubConfig(delegate.configName, delegate.commonConfigCallable.call());
				} catch (Exception e) {
					Throwables.propagate(e);
				}
			}
	}
	
	public void composeSettings(ClientSettings settings) {
		for(RegistryDelegate delegate : this.registeredLayers)
			if(delegate.clientConfigCallable != null) {
				try {
					settings.putSubConfig(delegate.configName, delegate.clientConfigCallable.call());
				} catch (Exception e) {
					Throwables.propagate(e);
				}
			}
	}

	public static final class RegistryDelegate {
		private StellarLayer layer;
		private Callable<IConfigHandler> clientConfigCallable;
		private Callable<INBTConfig> commonConfigCallable;
		private String configName;

		public RegistryDelegate commonConfig(Callable<INBTConfig> factory) {
			this.commonConfigCallable = factory;
			return this;
		}

		public RegistryDelegate clientConfig(Callable<IConfigHandler> factory) {
			this.clientConfigCallable = factory;
			return this;
		}
	}
}
