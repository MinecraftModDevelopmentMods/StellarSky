package stellarium.stellars.layer;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

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
	private Map<Class, String> layerNameMap = Maps.newHashMap();
	
	public StellarLayerRegistry() {
		this.registerLayer(new LayerBrStar(), null, (Callable)null, (Callable)null);
		this.registerLayer(new LayerMilkyway(), "MilkyWay", (Callable)null, MilkywaySettings.class);
		this.registerLayer(new LayerSolarSystem(), "SolarSystem", SolarSystemSettings.class, SolarSystemClientSettings.class);
		this.registerLayer(new LayerDeepSky(), "DeepSky", (Callable)null, (Callable)null);
	}
	
	public void registerLayer(IStellarLayerType layer, String configName, Callable<INBTConfig> commonConfigFactory, Callable<IConfigHandler> clientConfigFactory)
	{
		RegistryDelegate delegate = new RegistryDelegate();
		delegate.layer = layer;
		delegate.commonConfigCallable = commonConfigFactory;
		delegate.clientConfigCallable = clientConfigFactory;
		delegate.configName = configName;
		
		registeredLayers.add(delegate);
	}
	
	public void registerLayer(IStellarLayerType layer, String configName, Callable<INBTConfig> commonConfigFactory, Class<? extends IConfigHandler> clientConfigClass)
	{
		this.registerLayer(layer, configName,
				commonConfigFactory, new ClassInstantiateCallable(clientConfigClass));
	}
	
	public void registerLayer(IStellarLayerType layer, String configName, Class<? extends INBTConfig> commonConfigClass, Class<? extends IConfigHandler> clientConfigClass)
	{
		this.registerLayer(layer, configName,
				new ClassInstantiateCallable(commonConfigClass), new ClassInstantiateCallable(clientConfigClass));
	}
	
	public void composeLayer(boolean isRemote, List<StellarObjectContainer> list) {
		for(RegistryDelegate delegate : this.registeredLayers)
			try {
				list.add(new StellarObjectContainer(delegate.layer, delegate.configName));
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
	
	private class RegistryDelegate {
		private IStellarLayerType layer;
		private Callable<IConfigHandler> clientConfigCallable;
		private Callable<INBTConfig> commonConfigCallable;
		private String configName;
		
		public int hashCode() {
			return layer.hashCode();
		}
	}
	
	private class ClassInstantiateCallable<T> implements Callable<T> {
		
		private Class<? extends T> theClass;
		
		public ClassInstantiateCallable(Class<? extends T> theClass) {
			this.theClass = theClass;
		}
		
		@Override
		public T call() throws Exception {
			return theClass.newInstance();
		}
	}

}
