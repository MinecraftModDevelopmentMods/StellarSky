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
import stellarium.stellars.star.brstar.LayerBrStar;
import stellarium.stellars.system.LayerSolarSystem;
import stellarium.stellars.system.SolarSystemSettings;

public class CelestialLayerRegistry {
	
	private static CelestialLayerRegistry INSTANCE;
	
	public static CelestialLayerRegistry getInstance() {
		if(INSTANCE == null)
			INSTANCE = new CelestialLayerRegistry();
		return INSTANCE;
	}
	
	private List<RegistryDelegate> registeredLayers = Lists.newArrayList();
	private Map<Class, String> layerNameMap = Maps.newHashMap();
	
	public CelestialLayerRegistry() {
		this.registerLayer(true, LayerBrStar.class);
		this.registerLayer(true, LayerMilkyway.class);
		this.registerLayer(false, LayerSolarSystem.class, "SolarSystem", SolarSystemSettings.class);
	}
	
	public String getConfigName(ICelestialLayer layer) {
		return layerNameMap.get(layer.getClass());
	}
	
	public void registerLayer(boolean isClientOnly, Class<? extends ICelestialLayer> layerClass)
	{
		this.registerLayer(isClientOnly, layerClass, null, null);
	}
	
	public void registerLayer(boolean isClientOnly, Class<? extends ICelestialLayer> layerClass, String configName, Class<? extends IConfigHandler> configClass)
	{
		RegistryDelegate delegate = new RegistryDelegate();
		delegate.isClientOnly = isClientOnly;
		delegate.layerClass = layerClass;
		delegate.configClass = configClass;
		delegate.configName = configName;
		
		layerNameMap.put(layerClass, configName);
		
		registeredLayers.add(delegate);
	}
	
	public void composeLayer(List<ICelestialLayer> list) {
		for(RegistryDelegate delegate : this.registeredLayers)
			try {
				list.add(delegate.layerClass.newInstance());
			} catch (Exception e) {
				Throwables.propagateIfPossible(e);
			}
	}
	
	public void composeLayer(List<ICelestialLayer> list, boolean clientOnly) {
		for(RegistryDelegate delegate : this.registeredLayers)
			if(delegate.isClientOnly == clientOnly) {
				try {
					list.add(delegate.layerClass.newInstance());
				} catch (Exception e) {
					Throwables.propagateIfPossible(e);
				}
			}
	}
	
	public void composeSettings(CommonSettings settings) {
		for(RegistryDelegate delegate : this.registeredLayers)
			if(!delegate.isClientOnly && delegate.configName != null) {
				try {
					settings.putSubConfig(delegate.configName, (INBTConfig) delegate.configClass.newInstance());
				} catch (Exception e) {
					Throwables.propagate(e);
				}
			}
	}
	
	public void composeSettings(ClientSettings settings) {
		for(RegistryDelegate delegate : this.registeredLayers)
			if(delegate.isClientOnly && delegate.configName != null) {
				try {
					settings.putSubConfig(delegate.configName, (INBTConfig) delegate.configClass.newInstance());
				} catch (Exception e) {
					Throwables.propagate(e);
				}
			}
	}
	
	private class RegistryDelegate {
		private Class<? extends ICelestialLayer> layerClass;
		private Class<? extends IConfigHandler> configClass;
		private String configName;
		private boolean isClientOnly;
		
		public int hashCode() {
			return layerClass.hashCode();
		}
	}

}
