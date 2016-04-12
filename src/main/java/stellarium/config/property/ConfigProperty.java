package stellarium.config.property;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public abstract class ConfigProperty {
	protected Property property;
	protected String configKey;
	protected String dataKey;
	
	private String comment;
		
	public ConfigProperty(String configKey, String dataKey) {
		this.configKey = configKey;
		this.dataKey = dataKey;
	}
	
	public void setupConfiguration(Configuration config, String category) {
		this.property = config.get(category, this.configKey, this.getDefaultValue(), null, this.getType());
	}
	
	protected abstract Property.Type getType();
	
	protected abstract String getDefaultValue();
	
	public String getConfigName() {
		return this.configKey;
	}
	
	public void setRequiresMcRestart(boolean start) {
		property.setRequiresMcRestart(start);
	}
	
	public void setRequiresWorldRestart(boolean start) {
		property.setRequiresWorldRestart(start);
	}
	
	public void setLanguageKey(String langkey) {
		property.setLanguageKey(langkey);
	}
	
	public void setComment(String comment) {
		property.comment = comment;
	}
	
	public void setValidValues(String[] validValues) {
		property.setValidValues(validValues);
	}
	
	public abstract void setAsDefault();
	public abstract void setAsProperty(ConfigProperty property);
	
	public abstract void loadFromConfig();
	public abstract void saveToConfig();
	
	public abstract void readFromNBT(NBTTagCompound compound);
	public abstract void writeToNBT(NBTTagCompound compound);
}
