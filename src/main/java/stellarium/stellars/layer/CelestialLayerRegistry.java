package stellarium.stellars.layer;

import java.util.List;
import java.util.Map;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import stellarium.client.ClientSettings;
import stellarium.common.CommonSettings;
import stellarium.config.IConfigHandler;
import stellarium.config.INBTConfig;
import stellarium.stellars.milkyway.LayerMilkyway;
import stellarium.stellars.milkyway.MilkywaySettings;
import stellarium.stellars.star.brstar.LayerBrStar;
import stellarium.stellars.system.LayerSolarSystem;
import stellarium.stellars.system.SolarSystemClientSettings;
import stellarium.stellars.system.SolarSystemSettings;

public class CelestialLayerRegistry {
	
	private static CelestialLayerRegistry INSTANCE;
	
	public static CelestialLayerRegistry getInstance() {
		if(INSTANCE == null)
			INSTANCE = new CelestialLayerRegistry();
		return INSTANCE;
	}
	
	private List<RegistryDelegateClient> registeredClientLayers = Lists.newArrayList();
	private List<RegistryDelegateCommon> registeredCommonLayers = Lists.newArrayList();
	private Map<Class, String> layerNameMap = Maps.newHashMap();
	
	public CelestialLayerRegistry() {
		this.registerClientLayer(LayerBrStar.class, null, null);
		this.registerClientLayer(LayerMilkyway.class, "MilkyWay", MilkywaySettings.class);
		this.registerCommonLayer(LayerSolarSystem.class, "SolarSystem", SolarSystemSettings.class, SolarSystemClientSettings.class);
	}
	
	public String getConfigName(ICelestialLayer layer) {
		return layerNameMap.get(layer.getClass());
	}
	
	public void registerClientLayer(Class<? extends ICelestialLayer> layerClass, String configName, Class<? extends IConfigHandler> configClass)
	{
		RegistryDelegateClient delegate = new RegistryDelegateClient();
		delegate.clientLayerClass = layerClass;
		delegate.clientConfigClass = configClass;
		delegate.configName = configName;
		
		layerNameMap.put(layerClass, configName);
		
		registeredClientLayers.add(delegate);
	}
	
	public void registerCommonLayer(Class<? extends ICelestialLayerCommon> layerClass, String configName, Class<? extends INBTConfig> commonConfigClass, Class<? extends IConfigHandler> clientConfigClass)
	{
		RegistryDelegateCommon delegate = new RegistryDelegateCommon();
		delegate.commonLayerClass = layerClass;
		delegate.commonConfigClass = commonConfigClass;
		delegate.clientConfigClass = clientConfigClass;
		delegate.configName = configName;
		
		layerNameMap.put(layerClass, configName);
		
		registeredCommonLayers.add(delegate);
	}
	
	public void composeClientLayer(List<ICelestialLayer> list, boolean clientOnly) {
		for(RegistryDelegateClient delegate : this.registeredClientLayers)
			try {
				list.add(delegate.clientLayerClass.newInstance());
			} catch (Exception e) {
				Throwables.propagateIfPossible(e);
			}
	}
	
	public void composeCommonLayer(List<ICelestialLayerCommon> list, boolean clientOnly) {
		for(RegistryDelegateCommon delegate : this.registeredCommonLayers)
			try {
				list.add(delegate.commonLayerClass.newInstance());
			} catch (Exception e) {
				Throwables.propagateIfPossible(e);
			}
	}
	
	public void composeSettings(CommonSettings settings) {
		for(RegistryDelegateCommon delegate : this.registeredCommonLayers)
			if(delegate.commonConfigClass != null) {
				try {
					settings.putSubConfig(delegate.configName, delegate.commonConfigClass.newInstance());
				} catch (Exception e) {
					Throwables.propagate(e);
				}
			}
	}
	
	public void composeSettings(ClientSettings settings) {
		for(RegistryDelegateClient delegate : this.registeredClientLayers)
			if(delegate.clientConfigClass != null) {
				try {
					settings.putSubConfig(delegate.configName, delegate.clientConfigClass.newInstance());
				} catch (Exception e) {
					Throwables.propagate(e);
				}
			}
		
		for(RegistryDelegateCommon delegate : this.registeredCommonLayers)
			if(delegate.clientConfigClass != null) {
				try {
					settings.putSubConfig(delegate.configName, delegate.clientConfigClass.newInstance());
				} catch (Exception e) {
					Throwables.propagate(e);
				}
			}
	}
	
	private class RegistryDelegateClient {
		private Class<? extends ICelestialLayer> clientLayerClass;
		private Class<? extends IConfigHandler> clientConfigClass;
		private String configName;
		
		public int hashCode() {
			return clientLayerClass.hashCode();
		}
	}
	
	private class RegistryDelegateCommon {
		private Class<? extends ICelestialLayerCommon> commonLayerClass;
		private Class<? extends IConfigHandler> clientConfigClass;
		private Class<? extends INBTConfig> commonConfigClass;
		private String configName;
		
		public int hashCode() {
			return commonLayerClass.hashCode();
		}
	}

}
