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
import stellarium.stellars.display.DisplaySettings;
import stellarium.stellars.display.LayerDisplay;
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
		this.registerLayer(new LayerBrStar(), null, null, null);
		this.registerLayer(new LayerMilkyway(), "MilkyWay", null, MilkywaySettings.class);
		this.registerLayer(new LayerSolarSystem(), "SolarSystem", SolarSystemSettings.class, SolarSystemClientSettings.class);
		this.registerLayer(new LayerDisplay(), "Display", null, DisplaySettings.class);
	}
	
	public void registerLayer(IStellarLayerType layer, String configName, Class<? extends INBTConfig> commonConfigClass, Class<? extends IConfigHandler> clientConfigClass)
	{
		RegistryDelegate delegate = new RegistryDelegate();
		delegate.layer = layer;
		delegate.commonConfigClass = commonConfigClass;
		delegate.clientConfigClass = clientConfigClass;
		delegate.configName = configName;
				
		registeredLayers.add(delegate);
	}
	
	public void registerRenderers() {
		// TODO prevent duplication from inherited renderers
		for(RegistryDelegate delegate : this.registeredLayers)
			delegate.layer.registerRenderers();
	}
	
	public void composeLayer(boolean isRemote, List<StellarObjectContainer> list) {
		for(RegistryDelegate delegate : this.registeredLayers)
			try {
				list.add(new StellarObjectContainer(isRemote, delegate.layer, delegate.configName));
			} catch (Exception e) {
				Throwables.propagateIfPossible(e);
			}
	}
	
	public void composeSettings(CommonSettings settings) {
		for(RegistryDelegate delegate : this.registeredLayers)
			if(delegate.commonConfigClass != null) {
				try {
					settings.putSubConfig(delegate.configName, delegate.commonConfigClass.newInstance());
				} catch (Exception e) {
					Throwables.propagate(e);
				}
			}
	}
	
	public void composeSettings(ClientSettings settings) {
		for(RegistryDelegate delegate : this.registeredLayers)
			if(delegate.clientConfigClass != null) {
				try {
					settings.putSubConfig(delegate.configName, delegate.clientConfigClass.newInstance());
				} catch (Exception e) {
					Throwables.propagate(e);
				}
			}
	}
	
	private class RegistryDelegate {
		private IStellarLayerType layer;
		private Class<? extends IConfigHandler> clientConfigClass;
		private Class<? extends INBTConfig> commonConfigClass;
		private String configName;
		
		public int hashCode() {
			return layer.hashCode();
		}
	}

}
