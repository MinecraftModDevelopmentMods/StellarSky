package stellarium.compat;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraftforge.fml.common.Loader;

public class CompatManager {
	
	private static CompatManager instance;
	
	public static CompatManager getInstance() {
		if(instance == null)
			instance = new CompatManager();
		return instance;
	}
	
	private Map<String, ICompatModule> compatMap = Maps.newHashMap();
	private List<CompatModuleDelegate> loadedModules = Lists.newArrayList();
	
	public CompatManager() {
		//this.addCompatibility("CalendarAPI", new ModuleCalendarAPI());
	}
	
	public void addCompatibility(String modid, ICompatModule module) {
		compatMap.put(modid, module);
	}
	
	public void onPreInit() {
		for(Map.Entry<String, ICompatModule> entry : compatMap.entrySet()) {
			if(Loader.isModLoaded(entry.getKey()))
				loadedModules.add(new CompatModuleDelegate(entry.getKey(), entry.getValue()));
		}
		
		for(CompatModuleDelegate delegate : loadedModules)
			delegate.module.onPreInit();
	}
	
	public void onInit() {
		for(CompatModuleDelegate delegate : loadedModules)
			delegate.module.onInit();
	}
	
	public void onPostInit() {
		for(CompatModuleDelegate delegate : loadedModules)
			delegate.module.onPostInit();
	}

	private class CompatModuleDelegate {
		public String modid;
		public ICompatModule module;
		
		public CompatModuleDelegate(String key, ICompatModule value) {
			this.modid = key;
			this.module = value;
		}
	}
}
