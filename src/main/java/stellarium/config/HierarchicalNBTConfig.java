package stellarium.config;

import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;

public abstract class HierarchicalNBTConfig implements INBTConfig {

	private Map<String, INBTConfig> subConfigs = Maps.newHashMap();
	
	public void putSubConfig(String key, INBTConfig config) {
		subConfigs.put(key, config);
	}
	
	public INBTConfig getSubConfig(String key) {
		return subConfigs.get(key);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		for(Map.Entry<String, INBTConfig> entry : subConfigs.entrySet())
			entry.getValue().readFromNBT(compound.getCompoundTag(entry.getKey()));
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		for(Map.Entry<String, INBTConfig> entry : subConfigs.entrySet()) {
			NBTTagCompound subComp = new NBTTagCompound();
			entry.getValue().writeToNBT(subComp);
			compound.setTag(entry.getKey(), subComp);
		}
	}

	@Override
	public void setupConfig(Configuration config, String category) {
		for(Map.Entry<String, INBTConfig> entry : subConfigs.entrySet())
			entry.getValue().setupConfig(config, category + Configuration.CATEGORY_SPLITTER + entry.getKey());
	}

	@Override
	public void loadFromConfig(Configuration config, String category) {
		for(Map.Entry<String, INBTConfig> entry : subConfigs.entrySet())
			entry.getValue().loadFromConfig(config, category + Configuration.CATEGORY_SPLITTER + entry.getKey());
	}
	
	@Override
	public void saveToConfig(Configuration config, String category) {
		for(Map.Entry<String, INBTConfig> entry : subConfigs.entrySet())
			entry.getValue().saveToConfig(config, category + Configuration.CATEGORY_SPLITTER + entry.getKey());
	}

	@Override
	public abstract INBTConfig copy();
	
	protected void applyCopy(HierarchicalNBTConfig config) {
		for(Map.Entry<String, INBTConfig> entry : subConfigs.entrySet())
			config.putSubConfig(entry.getKey(), entry.getValue().copy());
	}

}
