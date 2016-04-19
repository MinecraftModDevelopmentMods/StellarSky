package stellarium.stellars.layer;

import java.util.List;
import java.util.Map;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import stellarapi.api.lib.config.IConfigHandler;
import stellarapi.api.lib.config.INBTConfig;
import stellarium.client.ClientSettings;
import stellarium.common.CommonSettings;
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
	
	private List<RegistryDelegateCommon> registeredCommonLayers = Lists.newArrayList();
	private Map<Class, String> layerNameMap = Maps.newHashMap();
	
	public CelestialLayerRegistry() {
		this.registerCommonLayer(LayerBrStar.class, null, null, null);
		this.registerCommonLayer(LayerMilkyway.class, "MilkyWay", null, MilkywaySettings.class);
		this.registerCommonLayer(LayerSolarSystem.class, "SolarSystem", SolarSystemSettings.class, SolarSystemClientSettings.class);
	}
	
	public String getConfigName(ICelestialLayer layer) {
		return layerNameMap.get(layer.getClass());
	}
	
	public void registerCommonLayer(Class<? extends ICelestialLayer> layerClass, String configName, Class<? extends INBTConfig> commonConfigClass, Class<? extends IConfigHandler> clientConfigClass)
	{
		RegistryDelegateCommon delegate = new RegistryDelegateCommon();
		delegate.commonLayerClass = layerClass;
		delegate.commonConfigClass = commonConfigClass;
		delegate.clientConfigClass = clientConfigClass;
		delegate.configName = configName;
		
		layerNameMap.put(layerClass, configName);
		
		registeredCommonLayers.add(delegate);
	}
	
	public void composeCommonLayer(List<ICelestialLayer> list) {
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
		for(RegistryDelegateCommon delegate : this.registeredCommonLayers)
			if(delegate.clientConfigClass != null) {
				try {
					settings.putSubConfig(delegate.configName, delegate.clientConfigClass.newInstance());
				} catch (Exception e) {
					Throwables.propagate(e);
				}
			}
	}
	
	private class RegistryDelegateCommon {
		private Class<? extends ICelestialLayer> commonLayerClass;
		private Class<? extends IConfigHandler> clientConfigClass;
		private Class<? extends INBTConfig> commonConfigClass;
		private String configName;
		
		public int hashCode() {
			return commonLayerClass.hashCode();
		}
	}

}
