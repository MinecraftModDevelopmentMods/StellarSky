package stellarium.config.property;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Property.Type;

public class ConfigPropertyString extends ConfigProperty {
	
	private String defaultValue;
	private String currentValue;
	
	public ConfigPropertyString(String configKey, String dataKey, String defaultValue) {
		super(configKey, dataKey);
		this.currentValue = this.defaultValue = defaultValue;
	}
	
	public String getString() {
		return this.currentValue;
	}
	
	public void setString(String value) {
		this.currentValue = value;
	}
	
	@Override
	protected String getDefaultValue() {
		return this.defaultValue;
	}
	
	@Override
	public void setAsDefault() {
		this.currentValue = this.defaultValue;
	}
	
	@Override
	public void setAsProperty(ConfigProperty property) {
		if(property instanceof ConfigPropertyString)
			this.currentValue = ((ConfigPropertyString) property).currentValue;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		if(compound.hasKey(this.dataKey))
			this.currentValue = compound.getString(this.dataKey);
		else this.currentValue = this.defaultValue;
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		compound.setString(this.dataKey, this.currentValue);
	}

	@Override
	public void loadFromConfig() {
		this.currentValue = property.getString();
	}
	
	@Override
	public void saveToConfig() {
		property.set(this.currentValue);
	}

	@Override
	protected Type getType() {
		return Type.STRING;
	}
}
