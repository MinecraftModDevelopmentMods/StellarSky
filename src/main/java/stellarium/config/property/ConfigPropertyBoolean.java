package stellarium.config.property;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Property.Type;

public class ConfigPropertyBoolean extends ConfigProperty {
	
	private boolean defaultValue;
	private boolean currentValue;
	
	public ConfigPropertyBoolean(String configKey, String dataKey, boolean defaultValue) {
		super(configKey, dataKey);
		this.currentValue = this.defaultValue = defaultValue;
	}
	
	public boolean getBoolean() {
		return this.currentValue;
	}
	
	public void setBoolean(boolean value) {
		this.currentValue = value;
	}
	
	@Override
	protected String getDefaultValue() {
		return Boolean.toString(this.defaultValue);
	}
	
	@Override
	public void setAsDefault() {
		this.currentValue = this.defaultValue;
	}
	
	@Override
	public void setAsProperty(ConfigProperty property) {
		if(property instanceof ConfigPropertyBoolean)
			this.currentValue = ((ConfigPropertyBoolean) property).currentValue;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		if(compound.hasKey(this.dataKey))
			this.currentValue = compound.getBoolean(this.dataKey);
		else this.currentValue = this.defaultValue;
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		compound.setBoolean(this.dataKey, this.currentValue);
	}

	@Override
	public void loadFromConfig() {
		this.currentValue = property.getBoolean(this.defaultValue);
	}
	
	@Override
	public void saveToConfig() {
		property.set(this.currentValue);
	}

	@Override
	protected Type getType() {
		return Type.BOOLEAN;
	}
}
